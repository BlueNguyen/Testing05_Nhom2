package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPageBooking {
    private WebDriver driver;
    private WebDriverWait wait;

    private By emailField = By.id("email");
    private By passwordField = By.id("password");
    private By loginBtnModal = By.xpath("//button[@type='submit' and normalize-space()='Đăng nhập']");

    public LoginPageBooking(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void login(String email, String password) {

        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginBtnModal));
        safeClick(btn);

    }

    private void safeClick(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
