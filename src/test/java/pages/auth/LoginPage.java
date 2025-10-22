package pages.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Các locator
    private By loginIcon = By.xpath("//button[img[contains(@src,'6596121.png')]]");
    private By loginBtnPopup = By.xpath("(//button[normalize-space()='Đăng nhập'])[1]");
    private By emailField = By.name("email");
    private By passwordField = By.name("password");
    private By loginBtnModal = By.xpath("//button[@type='submit']");
    private By registerBtn = By.xpath("//button[contains(@class,'bg-main') and normalize-space()='Đăng ký']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Mở form đăng nhập
    public void openLoginForm() {
        wait.until(ExpectedConditions.elementToBeClickable(loginIcon)).click();
        wait.until(ExpectedConditions.elementToBeClickable(loginBtnPopup)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    // Thực hiện đăng nhập
    public void login(String email, String password) {
        openLoginForm();
        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginBtnModal));
        safeClick(btn);
    }

    public void logins(String email, String password) {

        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginBtnModal));
        safeClick(btn);

    }

    // Mở form đăng ký từ form đăng nhập
    public void openRegisterWithLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginIcon)).click();
        wait.until(ExpectedConditions.elementToBeClickable(loginBtnPopup)).click();
        wait.until(ExpectedConditions.elementToBeClickable(registerBtn)).click();
    }

    // Click an toàn
    private void safeClick(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
