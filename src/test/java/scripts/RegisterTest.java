package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.auth.RegisterPage;
import utils.ExcelReader;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(RegisterTest.class);

    @DataProvider(name ="testDataCapstone")
    public Object[][] testDataCapstone(){
        String filePath= "src/test/resources/testDataCapstone.xlsx";
        String sheetName= "Sheet2";
        int rowCount = 5;

        Object[][] data = new Object[rowCount][6];
        for (int i = 0; i < rowCount; i++) {
            data[i][0] = ExcelReader.getCellData(filePath, sheetName, i + 1, 0); // Name
            data[i][1] = ExcelReader.getCellData(filePath, sheetName, i + 1, 1); // Email
            data[i][2] = ExcelReader.getCellData(filePath, sheetName, i + 1, 2); // Password
            data[i][3] = ExcelReader.getCellData(filePath, sheetName, i + 1, 3); // Phone
            data[i][4] = ExcelReader.getCellData(filePath, sheetName, i + 1, 4); // Birthday
            data[i][5] = ExcelReader.getCellData(filePath, sheetName, i + 1, 5); // expectedResult

        }
        return data;

    }
    // test case đăng ký
    @Test(dataProvider = "testDataCapstone")
    public void registerTest(String name, String email, String password, String phone, String birthday, String expectedResult) {
        try {
            logger.info("Name: {}", name);
            logger.info("Email: {}", email);
            logger.info("Password: {}", password);
            logger.info("Phone: {}", phone);
            logger.info("Birthday: {}", birthday);

            RegisterPage registerPage = new RegisterPage(driver);
            registerPage.register(name, email, password, phone, birthday);
            Thread.sleep(3000);

            String actualMessage = registerPage.getRegisterMessage();
            logger.info("Thông báo thực tế là: {}", actualMessage);

            Assert.assertEquals(actualMessage.trim(), expectedResult.trim(), "Sai thông báo mong muốn");
            logger.info("✅ Test passed");

        } catch (Exception e) {
            logger.error("❌ Test failed", e);
            throw new RuntimeException(e);
        }
    }
    //test case ẩn hiện mật khẩu
    @Test
    public void togglePassword(){
        RegisterPage registerPage =  new RegisterPage(driver);
        registerPage.openRegisterForm();

        WebElement passwordField =  driver.findElement(By.xpath("//input[@id='password']"));
        passwordField.sendKeys("123456");

        String beforeValue = passwordField.getAttribute("value");
        WebElement eyeIcon = driver.findElement(By.xpath("//span[contains(@class,'anticon-eye')]"));

        //1
        driver.findElement(By.xpath("//span[contains(@class,'anticon-eye')]")).click();
        Assert.assertEquals(passwordField.getAttribute("type"),"text");
        Assert.assertEquals(passwordField.getAttribute("value"), beforeValue, "Giá trị mật khẩu thay đổi khi hiển thị!");

        //2
        driver.findElement(By.xpath("//span[contains(@class,'anticon-eye')]")).click();
        Assert.assertEquals(passwordField.getAttribute("type"), "password");
        Assert.assertEquals(passwordField.getAttribute("value"), beforeValue, "Giá trị mật khẩu thay đổi khi ẩn!");
    }

    // chọn ngày sinh bằng lịch
    @Test
    public void testSelectBirthdayFromCalendar() throws InterruptedException {
        RegisterPage registerPage = new RegisterPage(driver);
        String expectedDate = "01/01/2000";

        registerPage.selectBirthdayFromCalendar(expectedDate);
        String actualDate = registerPage.getBirthdayValue();

        logger.info("Ngày sinh mong đợi: {}", expectedDate);
        logger.info("Ngày sinh thực tế: {}", actualDate);

        Thread.sleep(2000);

        Assert.assertEquals(actualDate, expectedDate, "Ngày sinh hiển thị sai trong ô!");
    }

    // chọn ngày sinh trong tương lai
    @Test
    public void testSelectFutureBirthday() {
        RegisterPage registerPage = new RegisterPage(driver);
        String futureDate = "01/01/2099";

        registerPage.selectFutureBirthday(futureDate);
        String actualValue = registerPage.getBirthdayValue();
        String errorMessage = registerPage.getBirthdayErrorMessage();

        Assert.assertTrue(
                !actualValue.equals(futureDate) || !errorMessage.isEmpty(),
                "Vẫn chọn được ngày sinh tương lai hoặc không có cảnh báo lỗi!"
        );
    }

}
