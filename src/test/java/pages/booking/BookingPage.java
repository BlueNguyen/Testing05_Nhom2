package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class BookingPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(BookingPage.class);

    // --- Lưu message lỗi cuối cùng bắt được
    private String lastErrorMessage = "";

    // --- Locator chung ---
    private By avatarIcon = By.xpath("//img[@class='h-10']");
    private By loginMenu = By.xpath("//button[normalize-space()='Đăng nhập']");
    private By bookingBtn = By.xpath("//button[contains(@class,'bg-main') and contains(@class,'rounded-lg')]");
    private By bookingConfirmBtn = By.xpath("//button[normalize-space()='Xác nhận']");
    private By successMsg = By.xpath("//*[contains(text(),'Thêm mới thành công')]");
    private By errorMsg = By.xpath("//div[contains(@class,'ant-notification-notice-description')]");


    public BookingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Helper: click an toàn ---
    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception jsEx) {
                throw new RuntimeException("❌ Không click được element: " + locator, e);
            }
        }
    }

    // --- Actions ---
    public void selectDiaDiem(String diaDiem) {
        By diaDiemDynamic = By.xpath("//h2[normalize-space()='" + diaDiem + "']");
        clickElement(diaDiemDynamic);
    }

    public void selectPhong(String phong) {
        By phongDynamic = By.xpath("//p[contains(@class,'text-xl') and normalize-space()='" + phong + "']");
        clickElement(phongDynamic);
    }

    public void openLoginModal() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.inset-0")));
        } catch (Exception ignored) {}

        clickElement(avatarIcon);
        WebElement loginBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(loginMenu));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", loginBtn);
    }

    public void clickBooking() {
        clickElement(bookingBtn);

        try {
            WebElement toast = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(errorMsg));
            lastErrorMessage = toast.getText();
            logger.info("❌ Lỗi login hiển thị: {}", lastErrorMessage);
            return;
        } catch (TimeoutException te) {
            logger.info("⏳ Không có lỗi login → tiếp tục flow booking");
        }

        try {
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(bookingConfirmBtn));
            confirmBtn.click();
            logger.info("✅ Đã click nút Xác nhận booking");
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();", driver.findElement(bookingConfirmBtn));
                logger.info("✅ Đã click nút Xác nhận booking (JS)");
            } catch (Exception ex) {
                logger.error("❌ Không tìm thấy nút Xác nhận booking!", ex);
            }
        }
    }


    public String getSuccessMessage() {
        try {
            WebElement msgElement = wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg));
            return msgElement.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getErrorMessage() {
        return lastErrorMessage.isEmpty() ? "Không tìm thấy thông báo lỗi!" : lastErrorMessage;
    }





}

