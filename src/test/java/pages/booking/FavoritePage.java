package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class FavoritePage extends BaseBookingPage {

    public FavoritePage(WebDriver driver) {
        super(driver);
    }

    // --- Click trái tim yêu thích ---
    public void clickFavorite(String tenPhong) {
        logger.info("💖 Click trái tim yêu thích cho phòng '{}'", tenPhong);

        try {
            // Nếu có tab "Phòng yêu thích" hoặc "Đã đặt" thì click
            List<WebElement> tabs = driver.findElements(
                    By.xpath("//button[contains(.,'Phòng yêu thích') or contains(.,'Đã đặt')]")
            );
            if (!tabs.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabs.get(0));
                logger.info("🗂️ Đã click tab hiển thị danh sách phòng.");
                Thread.sleep(1500);
            }

            // Cuộn giữa trang cho dễ thấy
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
            Thread.sleep(1000);

            // --- Tìm phòng theo tiêu đề (trong thẻ h2) ---
            By phongLocator = By.xpath("//p[contains(@class,'text-xl') and normalize-space()='" + tenPhong + "']");

            WebElement phong = wait
                    .withTimeout(Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(phongLocator));

            // --- Click icon trái tim ---
            WebElement heart = phong.findElement(By.xpath(".//button[descendant::svg]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", heart);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", heart);

            logger.info("✅ Đã click trái tim yêu thích cho '{}'", tenPhong);

        } catch (Exception e) {
            logger.error("❌ Không tìm thấy phòng '{}' để click yêu thích: {}", tenPhong, e.getMessage());

            // In ra danh sách phòng đang hiển thị để dễ debug
            List<WebElement> roomNames = driver.findElements(By.xpath("//h2[contains(@class,'text-xl')]"));
            for (WebElement room : roomNames) {
                logger.info("🛏️ Phòng hiển thị: '{}'", room.getText());
            }

            throw new RuntimeException("Không tìm thấy phòng: " + tenPhong, e);
        }
    }

    // --- Kiểm tra trái tim có đang bật (đỏ) không ---
    public boolean isFavoriteActive(String tenPhong) {
        logger.info("🔍 Kiểm tra trạng thái trái tim của phòng '{}'", tenPhong);

        try {
            By heartLocator = By.xpath(
                    "//h2[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                            + tenPhong.toLowerCase() + "')]"
                            + "/ancestor::div[contains(@class,'relative')]"
                            + "//svg[contains(@fill,'red') or contains(@class,'text-red')]"
            );

            WebElement heart = wait
                    .withTimeout(Duration.ofSeconds(8))
                    .until(ExpectedConditions.visibilityOfElementLocated(heartLocator));

            boolean active = heart.isDisplayed();
            logger.info("❤️ Trạng thái yêu thích: {}", active ? "BẬT (đỏ)" : "TẮT");
            return active;

        } catch (TimeoutException e) {
            logger.warn("⚠️ Không tìm thấy icon trái tim đỏ cho phòng '{}'", tenPhong);
            return false;
        }
    }
}
