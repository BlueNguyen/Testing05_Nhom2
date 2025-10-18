package scripts;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.booking.BookingPage;
import pages.LoginPage;
import utils.ExcelReader;

public class BookingTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(BookingTest.class);

    String filePath = "src/test/resources/BookingData.xlsx";
    String sheetName = "data1";

    @DataProvider(name = "BookingData")
    public Object[][] getBookingData() {
        return ExcelReader.getData(filePath, sheetName);
    }

    // ✅ Case: Login rồi booking
    @Test(dataProvider = "BookingData", priority = 1)
    public void testBookingRoomFromExcel(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running test with data: Email={}, DiaDiem={}, Phong={}", email, diaDiem, phong);

        BookingPage bookingPage = new BookingPage(driver);

        // 🔑 Login trước
        logger.info("➡ Thực hiện login với email: {}", email);
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        // 🏠 Sau đó chọn địa điểm
        logger.info("➡ Chọn địa điểm: {}", diaDiem);
        bookingPage.selectDiaDiem(diaDiem);

        // 🛏️ Rồi chọn phòng
        logger.info("➡ Chọn phòng: {}", phong);
        bookingPage.selectPhong(phong);

        // ✅ Click đặt phòng
        logger.info("➡ Click đặt phòng");
        bookingPage.clickBooking();

        String successMessage = bookingPage.getSuccessMessage();
        logger.info("✅ Kết quả booking: {}", successMessage);

        Assert.assertTrue(successMessage.toLowerCase().contains("thành công"),
                "❌ Booking không thành công! Actual message: " + successMessage);
    }

    // ❌ Case: Booking khi chưa login
    @Test(dataProvider = "BookingData", priority = 2)
    public void testBookingRoomWithoutLogin(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running test (NO LOGIN) with data: DiaDiem={}, Phong={}", diaDiem, phong);

        BookingPage bookingPage = new BookingPage(driver);

        // 🏠 Chọn địa điểm
        logger.info("➡ Chọn địa điểm: {}", diaDiem);
        bookingPage.selectDiaDiem(diaDiem);

        // 🛏️ Chọn phòng
        logger.info("➡ Chọn phòng: {}", phong);
        bookingPage.selectPhong(phong);

        // ❌ Click booking mà chưa login
        logger.info("➡ Click đặt phòng khi chưa login");
        bookingPage.clickBooking();

        String errorMessage = bookingPage.getErrorMessage();
        logger.info("❌ Kết quả booking khi chưa login: {}", errorMessage);

// normalize về lowercase
        String normalized = errorMessage == null ? "" : errorMessage.toLowerCase();

        Assert.assertTrue(
                normalized.contains("vui lòng đăng nhập")
                        || normalized.contains("login"),
                "❌ Hệ thống không chặn booking khi chưa login! Actual message: " + errorMessage
        );

    }


}
