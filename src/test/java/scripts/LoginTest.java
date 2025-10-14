package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.auth.LoginPage;
import utils.ExcelReader;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(LoginTest.class);

    @DataProvider(name = "testDataCapstone")
    public Object[][] testDataCapstone() {
        String filePath = "src/test/resources/testDataCapstone.xlsx";
        String sheetName = "Sheet1";
        int rowCount = 4;

        Object[][] data = new Object[rowCount][3];
        for (int i = 0; i < rowCount; i++) {
            data[i][0] = ExcelReader.getCellData(filePath, sheetName, i + 1, 0);
            data[i][1] = ExcelReader.getCellData(filePath, sheetName, i + 1, 1);
            data[i][2] = ExcelReader.getCellData(filePath, sheetName, i + 1, 2);
        }
        return data;
    }


    @Test(dataProvider = "testDataCapstone")
    public void loginTest(String email, String password, String expectedResult) {
        try {
            logger.info("Đang test với email: {}", email);
            logger.info("Đang test với password: {}", password);
            LoginPage loginPage = new LoginPage(driver);
            loginPage.login(email,password);
            Thread.sleep(3000);

            Boolean isLogged= false;
            try{
                driver.findElement(By.xpath("//span[normalize-space()='Đăng nhập thành công']"));
                isLogged=true;
            }catch (Exception e) {
                isLogged = false;
            } logger.info("Kết quả thực tế");
            Assert.assertEquals(isLogged,Boolean.parseBoolean(expectedResult),"Sai kết quả mong muốn");
//           System.out.println("Test pass");
            logger.info("Test pass");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testPasswordTooShort(){
        try{
            driver.get("https://demo4.cybersoft.edu.vn/");

            LoginPage loginPage = new LoginPage(driver);
            loginPage.openLoginForm();

            driver.findElement(By.name("email")).sendKeys("quanghuy424@gmail.com");
            driver.findElement(By.name("password")).sendKeys("123");

            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
            String errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Mật khẩu quá ngắn')]")
            )).getText();
            Assert.assertTrue(errorMsg.contains("Mật khẩu quá ngắn"),
                    "Không hiện thông báo khi nhập mật khẩu quá ngắn");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testPasswordTooLong(){
        try{
            driver.get("https://demo4.cybersoft.edu.vn/");

            LoginPage loginPage = new LoginPage(driver);
            loginPage.openLoginForm();

            driver.findElement(By.name("email")).sendKeys("quanghuy424@gmail.com");
            driver.findElement(By.name("password")).sendKeys("123456789101112");

            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
            String errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Mật khẩu quá dài')]")
            )).getText();
            Assert.assertTrue(errorMsg.contains("Mật khẩu quá dài"),
                    "Không hiện thông báo khi nhập mật khẩu quá dài");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void loginWithEnter(){
        try{
            driver.get("https://demo4.cybersoft.edu.vn/");
            LoginPage loginPage = new LoginPage(driver);
            loginPage.openLoginForm();

            driver.findElement(By.name("email")).sendKeys("quanghuy424@gmail.com");
            driver.findElement(By.name("password")).sendKeys("123"+ Keys.ENTER);

            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
            Boolean isLogged = false;
            try{
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='Đăng nhập thành công']")));
                isLogged=true;

            } catch (Exception e) {
                isLogged=false;
            }
            Assert.assertTrue(isLogged, "Đăng nhập bằng phím Enter không thành công");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void openRegisterWithLogin(){
        try{
            driver.get("https://demo4.cybersoft.edu.vn/");
            LoginPage loginPage = new LoginPage(driver);
            loginPage.openRegisterWithLogin();
            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));

            Boolean isRegisterPage = false;
            try{
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Đăng ký tài khoản Airbnb']")));
                isRegisterPage=true;

            } catch (Exception e) {
                isRegisterPage=false;
            }
            Assert.assertTrue(isRegisterPage,"Chuyển hướng không thành công");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}