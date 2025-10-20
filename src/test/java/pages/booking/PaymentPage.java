package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;

public class PaymentPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(PaymentPage.class);

    // --- Locators ---
    private By btnPlus = By.xpath("//button[div[text()='+']]");
    private By btnMinus = By.xpath("//button[div[text()='-']]");
    private By guestCount = By.xpath("//div[contains(text(),'khách')]");
    private By roomPrice = By.xpath("//p[contains(text(),'$') and contains(text,'night')]");
    private By cleaningFee = By.xpath("//div[p[contains(text(),'Cleaning fee')]]//p[contains(text(),'$')]");
    private By totalPrice = By.xpath("//div[p[contains(text(),'Total before taxes')]]//p[contains(text(),'$')]");

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Helpers ---
    private double extractNumeric(String text) {
        text = text.replaceAll("[^0-9.]", "");
        if (text.isEmpty()) return 0;
        return Double.parseDouble(text);
    }

    private double extractPrice(By locator) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        return extractNumeric(text);
    }

    private int extractGuestCount() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(guestCount)).getText();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    private void clickPlus() {
        wait.until(ExpectedConditions.elementToBeClickable(btnPlus)).click();
    }

    private void clickMinus() {
        wait.until(ExpectedConditions.elementToBeClickable(btnMinus)).click();
    }

    // --- Trích xuất giá phòng có nhân số đêm ---
    private double extractRoomTotal() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(roomPrice)).getText();
        // Ví dụ: "$10 X 7 nights"
        String[] parts = text.split("X");
        if (parts.length < 2) {
            logger.warn("⚠️ Không tìm thấy số đêm trong giá phòng, trả về giá đơn vị.");
            return extractNumeric(text);
        }
        double pricePerNight = extractNumeric(parts[0]);
        int nights = (int) extractNumeric(parts[1]);
        return pricePerNight * nights;
    }

    // --- Kiểm tra công thức tổng ---
    public void verifyTotalCalculation() {
        double roomTotal = extractRoomTotal();
        double cleaning = extractPrice(cleaningFee);
        double total = extractPrice(totalPrice);
        double expected = roomTotal + cleaning;

        if (Math.abs(total - expected) > 0.1)
            throw new AssertionError("❌ Sai công thức tổng! Expected: " + expected + ", Actual: " + total);
        logger.info("✅ Tổng tiền ban đầu đúng: " + total + " = " + roomTotal + " + " + cleaning);
    }

    // --- Kiểm tra khi click + và - ---
    public void verifyGuestAndPriceChange() {
        int oldGuest = extractGuestCount();
        double oldTotal = extractPrice(totalPrice);

        // Nhấn +
        clickPlus();
        int newGuest = extractGuestCount();
        double newTotal = extractPrice(totalPrice);

        if (newGuest != oldGuest + 1)
            throw new AssertionError("❌ Số khách không tăng đúng: expected " + (oldGuest + 1) + ", actual " + newGuest);
        if (newTotal <= oldTotal)
            throw new AssertionError("❌ Tổng tiền không tăng sau khi nhấn +");

        logger.info("✅ Sau khi nhấn +: khách=" + newGuest + ", tổng=" + newTotal);

        // Nhấn -
        clickMinus();
        int guestAfterMinus = extractGuestCount();
        double totalAfterMinus = extractPrice(totalPrice);

        if (guestAfterMinus != newGuest - 1)
            throw new AssertionError("❌ Số khách không giảm đúng sau khi nhấn -");
        if (totalAfterMinus >= newTotal)
            throw new AssertionError("❌ Tổng tiền không giảm sau khi nhấn -");

        logger.info("✅ Sau khi nhấn -: khách=" + guestAfterMinus + ", tổng=" + totalAfterMinus);
    }

    // --- Kiểm tra hiển thị trang thanh toán ---
    public boolean isPaymentStepDisplayed() {
        try {
            By paymentStep = By.xpath("//h1[contains(text(),'Payment')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(paymentStep));
            logger.info("✅ Trang Payment hiển thị");
            return true;
        } catch (TimeoutException e) {
            logger.error("❌ Không hiển thị trang Payment");
            return false;
        }
    }

}
