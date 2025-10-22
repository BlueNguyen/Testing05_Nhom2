package scripts;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.booking.*;
import pages.LoginPage;
import utils.ExcelReader;

import java.time.Duration;

public class BookingTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(BookingTest.class);

    String filePath = "src/test/resources/BookingData.xlsx";
    String sheetName = "data1";

    @DataProvider(name = "BookingData")
    public Object[][] getBookingData() {
        return ExcelReader.getData(filePath, sheetName);
    }

    // ✅ Case 1: Login rồi booking
    @Test(dataProvider = "BookingData", priority = 1)
    public void testBookingRoomFromExcel(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running test with data: Email={}, DiaDiem={}, Phong={}", email, diaDiem, phong);

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Login
        logger.info("➡ Login với email: {}", email);
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        // 🏠 Chọn địa điểm
        bookingPage.selectDiaDiem(diaDiem);

        // 🛏️ Chọn phòng
        bookingPage.selectPhong(phong);

        // ✅ Đặt phòng
        bookingPage.clickBooking();

        String successMessage = bookingPage.getSuccessMessage();
        logger.info("✅ Kết quả booking: {}", successMessage);

        Assert.assertTrue(successMessage.toLowerCase().contains("thành công"),
                "❌ Booking không thành công! Actual: " + successMessage);

        // 💳 Kiểm tra có hiển thị bước thanh toán không
        PaymentPage paymentPage = new PaymentPage(driver);
        boolean isPaymentDisplayed = paymentPage.isPaymentStepDisplayed();

        Assert.assertTrue(isPaymentDisplayed, "❌ Không hiển thị bước thanh toán sau khi đặt phòng!");
        logger.info("✅ Trang thanh toán hiển thị sau khi đặt phòng thành công.");
    }


    // ❌ Case 2: Booking khi chưa login
    @Test(dataProvider = "BookingData", priority = 2)
    public void testBookingRoomWithoutLogin(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Test booking (NO LOGIN): DiaDiem={}, Phong={}", diaDiem, phong);

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.selectDiaDiem(diaDiem);
        bookingPage.selectPhong(phong);
        bookingPage.clickBooking();

        String errorMessage = bookingPage.getErrorMessage();
        logger.info("❌ Thông báo khi chưa login: {}", errorMessage);

        String normalized = errorMessage == null ? "" : errorMessage.toLowerCase();

        Assert.assertTrue(
                normalized.contains("vui lòng đăng nhập") || normalized.contains("login"),
                "❌ Hệ thống không chặn booking khi chưa login! Actual: " + errorMessage
        );
    }

    // 🗑️ Case 3: Hủy booking
    @Test(priority = 3, dataProvider = "BookingData")
    public void testCancelBooking(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running cancel booking test for email: {}", email);

        BookingPage bookingPage = new BookingPage(driver);
        CancelBookingPage cancelPage = new CancelBookingPage(driver);

        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        cancelPage.waitAndClickAvatarAgain();
        DashboardPage dashboard = new DashboardPage(driver);
        dashboard.openDashboard();
        cancelPage.openRoomByName(phong);
        cancelPage.clickCancelBooking();

        logger.info("✅ Đã huỷ phòng thành công");
    }

    // 💖 Case 4: Yêu thích phòng
    @Test(priority = 4, dataProvider = "BookingData")
    public void testYeuThichPhong(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running favorite room test for email: {}", email);

        BookingPage bookingPage = new BookingPage(driver);
        FavoritePage favoritePage = new FavoritePage(driver);

        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        favoritePage.openDashboard();
        favoritePage.clickFavorite(phong);

        Assert.assertTrue(favoritePage.isFavoriteActive(phong),
                "❌ Trái tim chưa đổi màu đỏ sau khi click!");
    }


    // 📅 Case 5: Chọn ngày nhận/trả phòng (sử dụng dropdown)
    @Test(priority = 5, dataProvider = "BookingData")
    public void testChonNgayBooking(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running chọn ngày nhận/trả phòng cho email: {}", email);

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Login
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        // 🏠 Chọn địa điểm và phòng
        bookingPage.selectDiaDiem(diaDiem);
        bookingPage.selectPhong(phong);

        // 📅 Chọn ngày
        bookingPage.chonNgayNhanTraPhong("20", "October", "2025", "5", "November", "2025");

        logger.info("✅ Đã chọn ngày nhận/trả phòng thành công");
    }

    // ⚠️ Case 6: Tìm kiếm, đặt phòng 2 lần → kiểm tra thông báo phòng đã đặt
    @Test(priority = 6, dataProvider = "BookingData")
    public void testTimKiemVaBookingLai(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running test tìm kiếm & booking lại cho email: {}", email);

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Login
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        // 🏠 Set cứng địa điểm và phòng
        diaDiem = "Cần Thơ";
        phong = "Closer home!!!!";

        // 🏠 Chọn địa điểm
        bookingPage.selectDiaDiem(diaDiem);

        // 📅 Chọn ngày và click Tìm kiếm
        bookingPage.clickSearch("25", "October", "2025", "5", "November", "2025");

        // 🛏️ Click “Đặt phòng” lần 1
        bookingPage.selectPhong(phong);
        bookingPage.clickBooking();
        logger.info("✅ Đã click đặt phòng lần 1: {} - {}", diaDiem, phong);

        // ⏳ Chờ chút cho backend cập nhật
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {}

        // 🔁 Quay lại trang chính bằng nút Home
        By homeBtn = By.xpath("//a[normalize-space()='Home']");
        WebElement home = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(homeBtn));
        home.click();
        logger.info("🏠 Đã click nút Home để quay lại trang chính");

        // 🏠 Chọn địa điểm
        bookingPage.selectDiaDiem(diaDiem);

        // 📅 Chọn ngày và click Tìm kiếm
        bookingPage.clickSearch("25", "October", "2025", "5", "November", "2025");

        // 🛏️ Click “Đặt phòng” lần 2
        bookingPage.selectPhong(phong);
        bookingPage.clickBooking();
        logger.info("✅ Đã click đặt phòng lần 2: {} - {}", diaDiem, phong);


        // 🧾 Lấy thông báo lỗi
        String message = bookingPage.getErrorMessage();
        logger.info("📩 Thông báo hiển thị: {}", message);

        if (message.toLowerCase().contains("đã được đặt") || message.toLowerCase().contains("phòng đã đặt")) {
            logger.info("✅ Test pass: Hệ thống báo phòng đã được đặt");
        } else {
            logger.error("❌ Test fail: Vẫn cho phép đặt phòng đã thuê");
            Assert.fail("Không có thông báo Phòng đã đặt");
        }
    }

    // Case 7: Kiểm tra dashboard lịch sử đặt phòng
    @Test(priority = 7)
    public void testDashboardAfterBooking() {
        logger.info("🔹 Kiểm tra thông tin đặt phòng trong Dashboard...");

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Đăng nhập bằng tài khoản cố định
        String email = "blue299@gmail.com";
        String password = "blue299";
        logger.info("➡ Đăng nhập với tài khoản: {}", email);
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        // 📋 Mở Dashboard
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openDashboard();

        // 🔍 Tìm và kiểm tra thông tin booking
        String phong = "Fisherman homestay";
        String diaDiem = "Phú Quốc";
        String ngayNhan = "30-10-2025";
        String ngayTra = "15-11-2025";
        String soKhach = "2 khách";

        logger.info("🔍 Tìm và kiểm tra thông tin phòng: {}", phong);
        dashboardPage.scrollToBooking(phong);
        dashboardPage.verifyBookingLocation(diaDiem);
        dashboardPage.clickRoomByName(phong);
        dashboardPage.verifyBookingDetails(ngayNhan, ngayTra, soKhach);

        logger.info("🎯 Tất cả thông tin đặt phòng hiển thị chính xác!");
    }

    @Test(priority = 8)
    public void testPaymentGuestAndPriceLogic() {

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Đăng nhập bằng tài khoản cố định
        String email = "blue299@gmail.com";
        String password = "blue299";
        logger.info("➡ Đăng nhập với tài khoản: {}", email);
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        PaymentPage paymentPage = new PaymentPage(driver);

        logger.info("➡ Bắt đầu kiểm tra thông tin số khách và giá tiền ");

        // ✅ Kiểm tra công thức tính tổng ban đầu
        paymentPage.verifyTotalCalculation();

        // ✅ Kiểm tra logic thay đổi số khách và giá
        paymentPage.verifyGuestAndPriceChange();

        logger.info("🎯 Hoàn tất kiểm tra logic khách và giá!");
    }


}
