package pages.signout;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class SignoutPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(SignoutPage.class);

    private String lastErrorMessage = "";

    // --- Locators ---
    private By avatarIcon = By.xpath("//img[@class='h-10']"); // icon mặc định khi chưa login
    private By loginButtonMenu = By.xpath("//button[normalize-space()='Đăng nhập']");
    private By userAvatar = By.xpath("//button[@id='user-menu-button']"); // avatar sau khi login
    private By signOutButton = By.xpath("//button[normalize-space()='Sign out']");
    private By loginButtonVisible = By.xpath("//button[normalize-space()='Đăng nhập']"); // check sau khi logout

    public SignoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Helper ---
    private void safeClick(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception jsEx) {
                throw new RuntimeException("Không thể click element: " + locator, e);
            }
        }
    }

    // --- Mở modal login ---
    public void openLoginModal() {
        try {
            logger.info("Đang mở modal đăng nhập...");
            safeClick(avatarIcon);
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButtonMenu));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
            logger.info("Đã mở modal đăng nhập thành công.");
        } catch (Exception e) {
            lastErrorMessage = "Không thể mở modal đăng nhập: " + e.getMessage();
            logger.error(lastErrorMessage);
            throw new RuntimeException(lastErrorMessage, e);
        }
    }

    // --- Mở menu user (sau khi login) ---
    public void openUserMenu() {
        try {
            logger.info("👤 Mở menu người dùng (sau khi login)...");
            safeClick(userAvatar);
        } catch (Exception e) {
            lastErrorMessage = "Không thể mở menu user: " + e.getMessage();
            logger.error(lastErrorMessage);
            throw new RuntimeException(lastErrorMessage, e);
        }
    }

    // --- Click Sign out ---
    public void clickSignOut() {
        try {
            logger.info("Click Sign out...");
            safeClick(signOutButton);
            logger.info("Click Sign out thành công.");
        } catch (Exception e) {
            lastErrorMessage = "Không thể click Sign out: " + e.getMessage();
            logger.error(lastErrorMessage);
            throw new RuntimeException(lastErrorMessage, e);
        }
    }

    // --- Kiểm tra trạng thái logout ---
    public boolean verifyLoggedOut() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginButtonVisible));
            logger.info("Đã hiển thị nút 'Đăng nhập' → Logout thành công.");
            return true;
        } catch (TimeoutException e) {
            lastErrorMessage = "Không thấy nút 'Đăng nhập' sau khi logout.";
            logger.error(lastErrorMessage);
            return false;
        }
    }

    public String getErrorMessage() {
        return lastErrorMessage.isEmpty() ? "Không có lỗi nào được ghi nhận." : lastErrorMessage;
    }
}
