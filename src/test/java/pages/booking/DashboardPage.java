package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Locators ---
    private By avatarIcon = By.xpath("//img[@alt='avatar' or contains(@class,'rounded-full')]");
    private By dashboardLink = By.xpath("//a[@href='/info-user' and normalize-space()='Dashboard']");

    // --- Locators ---
    // Chọn toàn bộ card bao quanh p
    // Bắt đúng toàn thẻ a chứa thông tin phòng
    private By roomCards = By.xpath("//a[contains(@href,'room-detail') and descendant::p[contains(@class,'text-xl')]]");
    ;
    private By roomName  = By.xpath(".//p[contains(@class,'text-xl')]");



    private By roomLocation = By.xpath(".//p[contains(@class,'text-gray-500')][1]");


    // --- Booking detail locators ---
    private By nhanPhongField = By.xpath("//div[contains(text(),'Nhận phòng')]/following::div[1]");
    private By traPhongField = By.xpath("//div[contains(text(),'Trả phòng')]/following::div[1]");
    private By soKhachField = By.xpath("//div[contains(@class,'font-bold') and text()='Khách']/following::div//div");

    // --- Methods ---
    public void openDashboard() {
        logger.info("⏳ Chờ giao diện sau khi login hiển thị avatar...");
        // Đợi avatar thực sự xuất hiện (đảm bảo login xong)
        WebElement avatar = wait.until(ExpectedConditions.visibilityOfElementLocated(avatarIcon));
        wait.until(ExpectedConditions.elementToBeClickable(avatarIcon));

        try {
            logger.info("⏸️ Đợi thêm 5 giây để giao diện ổn định...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("🔹 Click avatar để mở menu người dùng...");
        avatar.click();

        // Chờ menu xổ xuống thật sự
        logger.info("⏳ Chờ popup menu người dùng hiển thị...");
        WebElement dashboard = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardLink));

        logger.info("🔹 Click Dashboard...");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dashboard);

        // Chờ Dashboard load xong
        logger.info("⏳ Chờ trang Dashboard load danh sách phòng...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(roomCards));
        logger.info("✅ Dashboard đã load danh sách phòng.");

    }


    public void scrollToBooking(String roomNameText) {
        logger.info("🔍 Cuộn để tìm phòng: {}", roomNameText);

        List<WebElement> cards = driver.findElements(roomCards);
        logger.info("🔹 Số thẻ roomCards tìm thấy: {}", cards.size());

        boolean found = false;
        for (WebElement card : cards) {
            try {
                String title = card.findElement(roomName).getText().trim();
                logger.info("➡ Tên phòng trong thẻ: {}", title);

                if (title.equalsIgnoreCase(roomNameText) || title.contains(roomNameText)) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", card);
                    logger.info("✅ Đã tìm thấy phòng: {}", title);
                    found = true;
                    break;
                }
            } catch (NoSuchElementException e) {
                logger.warn("⚠️ Không có thẻ <p> tên phòng trong card này!");
            }
        }

        if (!found)
            throw new NoSuchElementException("❌ Không tìm thấy phòng: " + roomNameText);
    }

    public void verifyBookingLocation(String expectedLocation) {
        String actualLocation = wait.until(ExpectedConditions.visibilityOfElementLocated(roomLocation)).getText().trim();
        logger.info("📍 Địa điểm hiển thị: " + actualLocation);
        if (!actualLocation.toLowerCase().contains(expectedLocation.toLowerCase())) {
            throw new AssertionError("❌ Sai địa điểm! Expected: " + expectedLocation + ", Actual: " + actualLocation);
        }
    }

    public void clickRoomByName(String targetName) {
        logger.info("⏳ Đang tìm phòng có tên chứa: {}", targetName);

        // Đợi danh sách phòng hiển thị
        List<WebElement> rooms = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(roomCards));

        boolean found = false;
        for (WebElement room : rooms) {
            String name = room.findElement(roomName).getText().trim();
            logger.info("🔹 Tên phòng tìm thấy: {}", name);

            if (name.equalsIgnoreCase(targetName) || name.contains(targetName)) {
                logger.info("🏠 Click vào phòng: {}", name);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", room);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", room);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new NoSuchElementException("❌ Không tìm thấy phòng có tên: " + targetName);
        }
    }


    public void verifyBookingDetails(String expectedNhan, String expectedTra, String expectedGuests) {
        logger.info("🔍 Kiểm tra chi tiết đặt phòng...");
        String actualNhan = wait.until(ExpectedConditions.visibilityOfElementLocated(nhanPhongField)).getText().trim();
        String actualTra = wait.until(ExpectedConditions.visibilityOfElementLocated(traPhongField)).getText().trim();
        String actualGuests = wait.until(ExpectedConditions.visibilityOfElementLocated(soKhachField)).getText().trim();

        logger.info("📅 Nhận: " + actualNhan + " | Trả: " + actualTra + " | Khách: " + actualGuests);

        if (!actualNhan.contains(expectedNhan))
            throw new AssertionError("❌ Ngày nhận sai! Expected: " + expectedNhan + ", Actual: " + actualNhan);
        if (!actualTra.contains(expectedTra))
            throw new AssertionError("❌ Ngày trả sai! Expected: " + expectedTra + ", Actual: " + actualTra);
        if (!actualGuests.contains(expectedGuests))
            throw new AssertionError("❌ Số khách sai! Expected: " + expectedGuests + ", Actual: " + actualGuests);

        logger.info("✅ Thông tin đặt phòng hiển thị chính xác!");
    }
}
