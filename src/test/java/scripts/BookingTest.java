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


    // ❌ Case 1: Booking khi chưa login
    @Test(dataProvider = "BookingData", priority = 1)
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

    // 📅 Case 2: Chọn ngày nhận/trả phòng (sử dụng dropdown)
    @Test(priority = 2, dataProvider = "BookingData")
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

    // ⚠️ Case 3: Tìm kiếm, đặt phòng 2 lần → kiểm tra thông báo phòng đã đặt
    @Test(priority = 3, dataProvider = "BookingData")
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

    // Case 4: Hiển thị chi phí đầy đủ (Số khách với giá tiền đúng)
    @Test(priority = 4)
    public void testPaymentGuestAndPriceLogic() {

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Đăng nhập bằng tài khoản cố định
        String email = "blueair@gmail.com";
        String password = "blue1234";
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

    //Case 5: Chọn số lượng khách và có cảnh báo số khách tối đa, tối thiểu
    @Test(priority = 5)
    public void testGuestCountLimits() throws InterruptedException {
        logger.info("🔹 Running testGuestCountLimits");

        BookingPage bookingPage = new BookingPage(driver);

        String email = "blueair@gmail.com";
        String password = "blue1234";
        String diaDiem = "Phú Quốc";
        String phong = "Fisherman homestay";

        bookingPage.openLoginModal();
        new LoginPage(driver).login(email, password);

        bookingPage.selectDiaDiem(diaDiem);
        bookingPage.clickSearchWithoutDate();
        bookingPage.selectPhong(phong);

        // --- Kiểm tra cảnh báo số khách tối đa ---
        boolean maxWarningShown = false;
        int attempts = 0;
        while (!maxWarningShown && attempts < 10) {
            bookingPage.clickIncreaseGuest();
            Thread.sleep(500);
            maxWarningShown = bookingPage.isGuestWarningDisplayed();
            logger.info("🔹 Tăng khách lần {} → Cảnh báo hiển thị: {}", ++attempts, maxWarningShown);
        }
        Assert.assertTrue(maxWarningShown, "❌ Không thấy cảnh báo số khách tối đa sau 10 lần tăng!");

        // --- Kiểm tra cảnh báo số khách tối thiểu ---
        bookingPage.openGuestSelector(); // ✅ Mở lại popup chọn khách

        boolean minWarningShown = false;
        attempts = 0;
        while (!minWarningShown && attempts < 10) {
            bookingPage.clickDecreaseGuest();
            Thread.sleep(500);
            minWarningShown = bookingPage.isGuestMinWarningDisplayed();
            logger.info("🔹 Giảm khách lần {} → Cảnh báo hiển thị: {}", ++attempts, minWarningShown);
        }

    }

    // 🧩 Case 6: Xác định trạng thái đặt phòng, loại A  "Đang chờ xác nhận từ chủ nhà", loại B  "Đặt thành công"
    @Test(priority = 6, description = "Xác định trạng thái đặt phòng cho loại A và B")
    public void testBookingStatusForDifferentRoomTypes() {
        String email = "blueair@gmail.com";
        String password = "blue1234";

        BookingPage bookingPage = new BookingPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        BaseBookingPage baseBookingPage = new BaseBookingPage(driver);

        // --- Login ---
        bookingPage.openLoginModal();
        LoginPageBooking loginPage = new LoginPageBooking(driver);
        loginPage.login(email, password);

        // 🔹 Test cho phòng loại A
        logger.info("===== Kiểm tra trạng thái đặt phòng loại A =====");
        bookingPage.selectDiaDiem("Đà Nẵng");
        bookingPage.selectPhong("Phòng loại A");
        bookingPage.chonNgayNhanTraPhong("30", "October", "2025", "12", "November", "2025");
        bookingPage.selectGuestCount("2 khách");
        bookingPage.clickBooking();

        String msgLoaiA = bookingPage.getSuccessMessage();
        logger.info("Thông báo sau đặt phòng loại A: {}", msgLoaiA);
        Assert.assertTrue(
                msgLoaiA.contains("Đang chờ xác nhận") || msgLoaiA.contains("chờ chủ nhà"),
                "❌ Phòng loại A không hiển thị trạng thái chờ xác nhận!"
        );

        // 🔹 Test cho phòng loại B
        logger.info("===== Kiểm tra trạng thái đặt phòng loại B =====");
        bookingPage.selectDiaDiem("Hà Nội");
        bookingPage.selectPhong("Phòng loại B");
        bookingPage.chonNgayNhanTraPhong("25", "October", "2025", "17", "November", "2025");
        bookingPage.selectGuestCount("2 khách");
        bookingPage.clickBooking();

        String msgLoaiB = bookingPage.getSuccessMessage();
        logger.info("Thông báo sau đặt phòng loại B: {}", msgLoaiB);
        Assert.assertTrue(
                msgLoaiB.contains("Đặt thành công") || msgLoaiB.contains("Thêm mới thành công"),
                "❌ Phòng loại B không hiển thị 'Đặt thành công'!"
        );

        // --- Mở dashboard kiểm tra ---
        baseBookingPage.openDashboard();
        dashboardPage.scrollToBooking("Phòng loại A");
        dashboardPage.scrollToBooking("Phòng loại B");
    }


    // ✅ Case 7: Login rồi booking, kiểm tra trang thanh toán có không
    @Test(dataProvider = "BookingData", priority = 7)
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

    // Case 8: Kiểm tra dashboard lịch sử đặt phòng
    @Test(priority = 8)
    public void testDashboardAfterBooking() {
        logger.info("🔹 Kiểm tra thông tin đặt phòng trong Dashboard...");

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Đăng nhập bằng tài khoản cố định
        String email = "blueair@gmail.com";
        String password = "blue1234";
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

    // 🗑️ Case 9: Hủy booking
    @Test(priority = 9, dataProvider = "BookingData")
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

    // 💖 Case 10: Yêu thích phòng
    @Test(priority = 10, dataProvider = "BookingData")
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







}
