package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.time.Duration;

public class BaseBookingPage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final Logger logger = LogManager.getLogger(BaseBookingPage.class);

    protected By userAvatar = By.xpath("//img[contains(@class,'rounded-full') and contains(@class,'object-cover')]");

    public BaseBookingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Hàm dùng chung: click an toàn ---
    protected void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);
            element.click();
        } catch (Exception e) {
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception ex) {
                throw new RuntimeException("❌ Không click được element: " + locator, ex);
            }
        }
    }

    // --- Hàm mở Dashboard (dùng lại được ở mọi page) ---
    public void openDashboard() {
        logger.info("➡ Mở Dashboard từ avatar menu");

        By dashboardLocator = By.xpath("//a[contains(@href,'info-user') or normalize-space()='Dashboard']");
        boolean opened = false;

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(userAvatar));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", avatar);

                Actions actions = new Actions(driver);
                actions.moveToElement(avatar).pause(Duration.ofMillis(300)).perform();

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", avatar);
                logger.info("🖱️ Click avatar (lần {})", attempt);

                WebElement dashboard = wait
                        .withTimeout(Duration.ofSeconds(5))
                        .until(ExpectedConditions.visibilityOfElementLocated(dashboardLocator));

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", dashboard);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dashboard);
                logger.info("✅ Đã click Dashboard (lần {})", attempt);

                wait.until(ExpectedConditions.urlContains("/info-user"));
                logger.info("✅ Dashboard mở thành công, URL: {}", driver.getCurrentUrl());
                opened = true;
                break;

            } catch (TimeoutException e) {
                logger.warn("⚠️ Menu chưa hiện (lần {}) → thử lại sau 3s", attempt);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!opened) {
            String msg = "❌ Menu avatar không mở được sau 3 lần thử";
            logger.error(msg);
            Assert.fail(msg);
        }
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
}
