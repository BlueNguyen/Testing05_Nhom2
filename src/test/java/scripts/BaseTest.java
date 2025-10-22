package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.DriverFactory;

import java.time.Duration;

public class BaseTest {
    public WebDriver driver;

    @BeforeMethod
    public void setUp() {
        System.out.println("🧠 [Setup] Đang khởi tạo driver...");
        driver = DriverFactory.getDriver();
        System.out.println("🚗 [Setup] Driver khởi tạo xong!");

        try {
            driver.get("https://demo4.cybersoft.edu.vn/");
            System.out.println("🌐 [Setup] Đã mở website thành công!");
        } catch (Exception e) {
            System.err.println("❌ [Setup] Lỗi khi mở trang: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @AfterMethod
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }

    }
}
