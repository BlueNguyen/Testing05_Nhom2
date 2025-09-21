package scripts;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BookingPage;
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

    @Test(dataProvider = "BookingData")
    public void testBookingRoomFromExcel(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Running test with data: Email={}, DiaDiem={}, Phong={}", email, diaDiem, phong);

        BookingPage bookingPage = new BookingPage(driver);

        logger.info("➡ Chọn địa điểm: {}", diaDiem);
        bookingPage.selectDiaDiem(diaDiem);

        logger.info("➡ Chọn phòng: {}", phong);
        bookingPage.selectPhong(phong);

        logger.info("➡ Thực hiện login với email: {}", email);
        bookingPage.openLoginModal();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        logger.info("➡ Click đặt phòng");
        bookingPage.clickBooking();

        String successMessage = bookingPage.getSuccessMessage();
        logger.info("✅ Kết quả booking: {}", successMessage);

        Assert.assertTrue(successMessage.contains("Thêm mới thành công"),
                "❌ Booking không thành công! Actual message: " + successMessage);
    }


}
