package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.testng.Assert;
import java.time.Duration;

public class CancelBookingPage extends BaseBookingPage {

    private static final Logger logger = LogManager.getLogger(CancelBookingPage.class);
    private By cancelBookingButton = By.xpath("//button[normalize-space()='Huỷ phòng']");

    public CancelBookingPage(WebDriver driver) {
        super(driver); // kế thừa driver và wait
    }

    // --- Click lại avatar sau khi login ---
    public void waitAndClickAvatarAgain() {
        try {
            logger.info("⏳ Đợi 5s sau khi login để giao diện ổn định...");
            Thread.sleep(5000);

            WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(userAvatar));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", avatar);
            avatar.click();

            logger.info("🟢 Đã click lại avatar sau khi login.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("❌ Thread bị ngắt khi sleep: {}", e.getMessage());
        } catch (Exception e) {
            logger.warn("⚠ Không click được avatar sau khi login: {}", e.getMessage());
        }
    }

    // --- Mở phòng theo tên ---
    public void openRoomByName(String phong) {
        logger.info("➡ Tìm phòng có tên: {}", phong);
        By roomName = By.xpath("//p[contains(@class,'text-xl') and normalize-space()='" + phong + "']");
        clickElement(roomName); // dùng lại từ BaseBookingPage
        logger.info("✅ Đã click vào phòng: {}", phong);
    }

    // --- Click nút Cancel booking ---
    public void clickCancelBooking() {
        logger.info("➡ Tìm và click nút Huỷ phòng");

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cancelBtn;

        try {
            cancelBtn = shortWait.until(ExpectedConditions.elementToBeClickable(cancelBookingButton));
        } catch (TimeoutException e) {
            String msg = "❌ Không tìm thấy Button Huỷ phòng";
            logger.error(msg);
            Assert.fail(msg);
            return;
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cancelBtn);
        cancelBtn.click();
        logger.info("✅ Đã click vào nút Huỷ phòng");

        // 🔁 Xác nhận popup nếu có
        By confirmCancel = By.xpath("//button[contains(.,'Confirm') or contains(.,'Xác nhận')]");
        try {
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmCancel));
            confirmBtn.click();
            logger.info("✅ Đã xác nhận huỷ phòng");
        } catch (TimeoutException e) {
            logger.warn("⚠ Không thấy popup xác nhận (bỏ qua nếu không có)");
        }
    }
}
