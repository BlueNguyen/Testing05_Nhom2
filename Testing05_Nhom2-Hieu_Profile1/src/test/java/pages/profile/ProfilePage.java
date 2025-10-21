package pages.profile;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;

public class ProfilePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(ProfilePage.class);

    // --- Locators ---
    private final By avatarIcon = By.xpath("//img[contains(@class,'h-10') or contains(@alt,'avatar')]");
    private final By loginMenu = By.xpath("//button[normalize-space()='Đăng nhập']");
    private final By dashboardBtn = By.xpath("//a[contains(@href,'/info-user') or text()='Dashboard']");
    private final By uploadAvatarBtn = By.xpath("//button[contains(.,'Cập nhật ảnh') or contains(.,'Đổi ảnh') or contains(.,'Thay đổi')]");
    private final By uploadInput = By.xpath("//input[@type='file']");
    private final By uploadAvatarConfirmBtn = By.xpath("//button[contains(.,'Upload Avatar') or contains(.,'Tải lên') or contains(.,'Upload')]");
    private final By editProfileBtn = By.xpath("//button[contains(.,'Chỉnh sửa hồ sơ') or contains(.,'Sửa thông tin')]");
    private final By saveProfileBtn = By.xpath("//button[normalize-space()='Cập nhật' or normalize-space()='Lưu']");
    private final By toastLocator = By.xpath("//div[contains(@class,'toast') or contains(@class,'ant-notification-notice')]");

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // --- Helper click (ổn định hơn với scroll + JS fallback) ---
    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            try {
                element.click();
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        } catch (Exception e) {
            logger.error("❌ Không click được element: {}", locator, e);
            throw new RuntimeException("Không thể click element: " + locator, e);
        }
    }

    // --- 1️⃣ Mở modal đăng nhập ---
    public void openLoginModal() {
        try {
            clickElement(avatarIcon);
            WebElement loginBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(loginMenu));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
            logger.info("🟢 Đã mở modal đăng nhập");
        } catch (Exception e) {
            logger.error("❌ Không thể mở modal đăng nhập", e);
            throw e;
        }
    }

    // --- 2️⃣ Điều hướng đến Dashboard ---
    public void goToDashboard() {
        clickElement(avatarIcon);
        clickElement(dashboardBtn);
        wait.until(ExpectedConditions.urlContains("/info-user"));
        logger.info("🟢 Đã điều hướng đến trang info-user");
    }

    // --- 3️⃣ Upload Avatar ---
    public void uploadAvatar(String imagePath) {
        try {
            // Mở modal upload (nếu có nút để mở)
            try {
                clickElement(uploadAvatarBtn);
            } catch (Exception ignore) {
                logger.info("⚠ Không có nút mở modal upload, có thể input hiển thị sẵn");
            }

            WebElement uploadElem = wait.until(ExpectedConditions.presenceOfElementLocated(uploadInput));
            uploadElem.sendKeys(imagePath);
            logger.info("📸 Đã chọn file upload: {}", imagePath);

        } catch (Exception e) {
            logger.error("❌ Lỗi khi upload avatar", e);
            throw e;
        }
    }

    // --- Click nút Upload Avatar ---
    public void clickUploadAvatarButton() {
        try {
            WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(uploadAvatarConfirmBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", confirm);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirm);
            logger.info("✅ Click nút 'Upload Avatar' thành công");

            // Chờ modal biến mất
            wait.until(ExpectedConditions.invisibilityOfElementLocated(uploadAvatarConfirmBtn));
        } catch (TimeoutException e) {
            logger.error("❌ Hết thời gian chờ: Nút Upload Avatar không khả dụng", e);
        } catch (Exception e) {
            logger.error("❌ Không thể click nút Upload Avatar", e);
        }
    }

    // --- Lấy đường dẫn ảnh avatar hiện tại ---
    public String getAvatarSrc() {
        try {
            WebElement avatar = wait.until(ExpectedConditions.visibilityOfElementLocated(avatarIcon));
            return avatar.getAttribute("src");
        } catch (Exception e) {
            logger.warn("⚠ Không lấy được src avatar");
            return "";
        }
    }

    // --- 4️⃣ Chỉnh sửa hồ sơ ---
    public void editProfile(String name, String phone, String birthday) {
        try {
            clickElement(editProfileBtn);

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
            WebElement phoneField = driver.findElement(By.id("phone"));
            WebElement birthdayField = driver.findElement(By.id("birthday"));

            clearAndType(nameField, name);
            clearAndType(phoneField, phone);
            clearAndType(birthdayField, birthday);

            clickElement(saveProfileBtn);
            logger.info("📝 Đã cập nhật hồ sơ: {} - {} - {}", name, phone, birthday);
        } catch (Exception e) {
            logger.error("❌ Lỗi khi chỉnh sửa hồ sơ", e);
            throw e;
        }
    }

    // Helper: xóa và nhập text an toàn
    private void clearAndType(WebElement field, String text) {
        field.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        field.sendKeys(text);
    }

    // --- 5️⃣ Lấy thông báo toast ---
    public String getToastMessage() {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastLocator));
            String message = toast.getText().trim();
            logger.info("🔔 Thông báo: {}", message);
            return message;
        } catch (TimeoutException e) {
            logger.warn("⚠ Không thấy toast message hiển thị");
            return "";
        }
    }
}
