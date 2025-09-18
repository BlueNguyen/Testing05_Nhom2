package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BookingPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By diaDiemLink = By.xpath("//a[@href='/rooms/da-lat']//h2[normalize-space()='Đà Lạt']");
    private By phongPhuHop = By.xpath("//p[contains(@class,'text-xl') and normalize-space()='Phòng mùa hè']");
    private By avatarIcon = By.xpath("//img[@class='h-10']");
    private By loginMenu = By.xpath("//button[normalize-space()='Đăng nhập']");
    private By bookingBtn = By.xpath("//button[contains(@class,'bg-main') and contains(@class,'rounded-lg')]");
    private By bookingConfirmBtn = By.xpath("//button[normalize-space()='Xác nhận']");

    public BookingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // tăng timeout
    }

    // --- Helper để click an toàn ---
    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

            // Cuộn element ra giữa màn hình (instant, không smooth)
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);

            // Đợi đến khi thật sự click được
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();

        } catch (Exception e) {
            // Nếu click thường fail (do bị che, do animation), dùng JS click
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception jsEx) {
                throw new RuntimeException("❌ Không click được element: " + locator, e);
            }
        }
    }


    // --- Actions ---
    public void clickDiaDiem() {
        clickElement(diaDiemLink);
    }

    public void clickPhong() {

        clickElement(phongPhuHop);
    }


    public void openLoginModal() {
        // Đợi overlay (nếu có) biến mất
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.fixed.inset-0")));
        } catch (Exception ignored) {}

        // Click vào avatar để mở menu
        clickElement(avatarIcon);

        // Đợi cho menu hiển thị
        WebElement loginBtn = wait.until(ExpectedConditions
                .visibilityOfElementLocated(loginMenu));

        // Scroll và click bằng JS (tránh bị che)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", loginBtn);
    }

    public void clickBooking() {
        // Click nút "Đặt phòng"
        clickElement(bookingBtn);

        // Chờ overlay (modal loading) biến mất nếu có
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.fixed.inset-0")));
        } catch (Exception ignored) {}

        // Nếu xuất hiện popup "OK" thì xử lý
        try {
            WebElement okBtn = driver.findElement(By.xpath("//button[text()='OK']"));
            if (okBtn.isDisplayed()) {
                okBtn.click();
            }
        } catch (NoSuchElementException ignored) {
            // Không có popup thì bỏ qua
        }

        // Chờ confirm button hiển thị và có thể click
        WebElement confirmBtn = wait.until(ExpectedConditions
                .elementToBeClickable(bookingConfirmBtn));

        try {
            confirmBtn.click();
        } catch (Exception e) {
            // Nếu Selenium click fail (do che/animation) → fallback sang JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
        }
    }

}
