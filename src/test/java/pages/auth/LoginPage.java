package pages.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Mở form đăng nhập
    public void openLoginForm() {
        // B1: mở popup login bằng icon avatar
        By loginIcon = By.xpath("//button[img[contains(@src,'6596121.png')]]");
        wait.until(ExpectedConditions.elementToBeClickable(loginIcon)).click();

        // B2: click nút "Đăng nhập" đầu tiên trong popup
        By loginBtn = By.xpath("(//button[normalize-space()='Đăng nhập'])[1]");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();

        // B3: chờ trường email hiển thị
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
    }

    // Thực hiện đăng nhập
    public void login(String email, String password) throws InterruptedException {
        openLoginForm();
        driver.findElement(By.name("email")).sendKeys(email);
        Thread.sleep(2000);
        driver.findElement(By.name("password")).sendKeys(password);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    // Mở form đăng ký từ form đăng nhập
    public void openRegisterWithLogin() {
        // B1: mở popup login bằng icon avatar
        By loginIcon = By.xpath("//button[img[contains(@src,'6596121.png')]]");
        wait.until(ExpectedConditions.elementToBeClickable(loginIcon)).click();

        // B2: mở form đăng nhập
        By loginBtn = By.xpath("(//button[normalize-space()='Đăng nhập'])[1]");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();

        // B3: chuyển sang form đăng ký
        By registerBtn = By.xpath("//button[contains(@class,'bg-main') and normalize-space()='Đăng ký']");
        wait.until(ExpectedConditions.elementToBeClickable(registerBtn)).click();
    }
}
