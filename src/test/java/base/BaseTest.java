package base;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.DriverFactory;
import utils.ScreenshotUtil;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {
    public WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.getDriver();  // lấy driver từ DriverFactory

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://demo4.cybersoft.edu.vn/");
    }

    @AfterMethod
    public void tearDown(ITestResult result, Method method) {
        // Nếu test FAIL -> chụp screenshot
        if (result.getStatus() == ITestResult.FAILURE) {
            String testName = method.getName();
            ScreenshotUtil.captureScreenshot(driver, testName);
        }

//        if (driver != null) {
//            driver.quit();
//        }
    }
}
