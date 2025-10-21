package pages.booking;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;

public class BookingPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(BookingPage.class);
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

    // --- Helper click an toàn ---
    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
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
        By phongDynamic = By.xpath(
                "//p[contains(@class,'text-xl') and normalize-space()='" + phong + "']"
                        + " | "
                        + "//span[contains(@class,'truncate') and normalize-space()='" + phong + "']"
        );
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
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(bookingConfirmBtn));
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

    // --- Locators cho calendar ---
    private By calendarInput = By.xpath("(//div[contains(@class,'cursor-pointer') and contains(@class,'p-3')])[1]");
    private By monthDropdown = By.xpath("//span[@class='rdrMonthPicker']/select");
    private By yearDropdown  = By.xpath("//span[@class='rdrYearPicker']/select");
    private By closeButton   = By.xpath("//button[normalize-space()='Close' or normalize-space()='Đóng']");

    // --- Chọn tháng & năm bằng dropdown ---
    private void chonThangNam(String thang, String nam) {
        try {
            logger.info("📅 Chọn tháng và năm: {} {}", thang, nam);
            WebElement monthSelect = wait.until(ExpectedConditions.elementToBeClickable(monthDropdown));
            new Select(monthSelect).selectByVisibleText(thang);
            WebElement yearSelect = wait.until(ExpectedConditions.elementToBeClickable(yearDropdown));
            new Select(yearSelect).selectByVisibleText(nam);
            logger.info("✅ Đã chọn {} {}", thang, nam);
        } catch (Exception e) {
            logger.error("❌ Lỗi khi chọn tháng/năm: {}", e.getMessage());
            throw new RuntimeException("Không thể chọn tháng/năm", e);
        }
    }

    // --- Chọn ngày ---
    private void chonNgay(String ngay) {
        try {
            By dayLocator = By.xpath("//button[contains(@class,'rdrDay')]//span[@class='rdrDayNumber']/span[text()='" + ngay + "']");
            WebElement dayEl = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", dayEl);
            try {
                dayEl.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dayEl);
            }
            logger.info("✅ Đã chọn ngày {}", ngay);
        } catch (Exception e) {
            logger.error("❌ Không chọn được ngày {}: {}", ngay, e.getMessage());
            throw new RuntimeException("Không thể chọn ngày " + ngay, e);
        }
    }

    // --- ✅ Hàm chính: Chọn ngày nhận & trả phòng + kiểm tra hiển thị ---
    public void chonNgayNhanTraPhong(String ngayNhan, String thangNhan, String namNhan,
                                     String ngayTra, String thangTra, String namTra) {
        try {
            logger.info("📅 Bắt đầu chọn ngày nhận/trả phòng: {} {}/{} → {} {}/{}",
                    ngayNhan, thangNhan, namNhan, ngayTra, thangTra, namTra);

            // 1️⃣ Mở calendar
            WebElement calendar = wait.until(ExpectedConditions.presenceOfElementLocated(calendarInput));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", calendar);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", calendar);
            logger.info("✅ Đã mở calendar");

            // 2️⃣ Chọn Check-in
            chonThangNam(thangNhan, namNhan);
            chonNgay(ngayNhan);

            // 3️⃣ Chọn Check-out
            chonThangNam(thangTra, namTra);
            chonNgay(ngayTra);

            // 4️⃣ Đóng calendar
            try {
                WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(closeButton));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
            } catch (Exception ignored) {
                logger.warn("⚠️ Không tìm thấy nút Close, popup có thể đã tự đóng");
            }

            // 5️⃣ So sánh giá trị hiển thị
            String expectedNhan = String.format("%02d-%02d-%s",
                    Integer.parseInt(ngayNhan), getMonthNumber(thangNhan), namNhan);
            String expectedTra = String.format("%02d-%02d-%s",
                    Integer.parseInt(ngayTra), getMonthNumber(thangTra), namTra);

            By nhanPhongValue = By.xpath("//div[normalize-space(text())='Nhận phòng']/parent::div/div[2]");
            By traPhongValue = By.xpath("//div[normalize-space(text())='Trả phòng']/parent::div/div[2]");

            WebElement nhanEl = wait.until(ExpectedConditions.visibilityOfElementLocated(nhanPhongValue));
            WebElement traEl = wait.until(ExpectedConditions.visibilityOfElementLocated(traPhongValue));

            String actualNhan = nhanEl.getText().trim();
            String actualTra = traEl.getText().trim();

            logger.info("Ngày nhận hiển thị: {}", actualNhan);
            logger.info("Ngày trả hiển thị: {}", actualTra);



            if (actualNhan.equals(expectedNhan))
                logger.info("✅ Ngày nhận phòng hiển thị khớp: {}", actualNhan);
            else
                logger.warn("❌ Ngày nhận phòng không khớp! Expected: {}, Actual: {}", expectedNhan, actualNhan);

            if (actualTra.equals(expectedTra))
                logger.info("✅ Ngày trả phòng hiển thị khớp: {}", actualTra);
            else
                logger.warn("❌ Ngày trả phòng không khớp! Expected: {}, Actual: {}", expectedTra, actualTra);

        } catch (Exception e) {
            logger.error("❌ Lỗi khi chọn ngày nhận/trả phòng: {}", e.getMessage());
            throw new RuntimeException("Không thể chọn ngày nhận/trả phòng", e);
        }
    }

    // --- Helper chuyển tháng chữ sang số ---
    private int getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january": return 1;
            case "february": return 2;
            case "march": return 3;
            case "april": return 4;
            case "may": return 5;
            case "june": return 6;
            case "july": return 7;
            case "august": return 8;
            case "september": return 9;
            case "october": return 10;
            case "november": return 11;
            case "december": return 12;
            default: throw new IllegalArgumentException("Tháng không hợp lệ: " + monthName);
        }
    }

    // --- 🔍 Click nút Tìm kiếm (mở calendar, chọn ngày, click search) ---
    public void clickSearch(String ngayNhan, String thangNhan, String namNhan,
                            String ngayTra, String thangTra, String namTra) {
        try {
            logger.info("🔍 Bắt đầu chọn ngày nhận/trả và click Tìm kiếm");

            // 1️⃣ Mở calendar
            WebElement calendar = wait.until(ExpectedConditions.presenceOfElementLocated(calendarInput));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", calendar);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", calendar);
            logger.info("✅ Đã mở calendar");

            // 2️⃣ Chọn Check-in
            chonThangNam(thangNhan, namNhan);
            chonNgay(ngayNhan);

            // 🕐 Wait 5s cho UI update
            logger.info("⏳ Đợi 5s trước khi chọn ngày trả phòng...");
            Thread.sleep(5000);

            // 3️⃣ Chọn Check-out
            chonThangNam(thangTra, namTra);
            chonNgay(ngayTra);

            // 5️⃣ Click nút “Tìm kiếm”
            By searchBtn = By.xpath("//div[contains(@class,'bg-main') and contains(@class,'justify-center')]");
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(searchBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("✅ Đã click nút Tìm kiếm");

        } catch (Exception e) {
            logger.error("❌ Lỗi khi chọn ngày và click Tìm kiếm: {}", e.getMessage());
            throw new RuntimeException("Không thể thực hiện clickSearch()", e);
        }
    }


}
