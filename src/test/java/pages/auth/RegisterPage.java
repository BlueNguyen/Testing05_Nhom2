package pages.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public RegisterPage(WebDriver driver){
            this.driver=driver;
            this.wait=new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    //mở form đăng ký
    public void openRegisterForm(){
        By registerIcon = By.xpath("//button[img[contains(@src,\"6596121.png\")]]");
        wait.until(ExpectedConditions.elementToBeClickable(registerIcon)).click();

        By registerBtn = By.xpath("//button[normalize-space()=\"Đăng ký\"][1]");
        wait.until(ExpectedConditions.elementToBeClickable(registerBtn)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id=\"name\"]")));
    }

    //locator form đăng ký
    public void register(String name, String email, String password, String phone, String birthday) {
        openRegisterForm();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='name']"))).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='email']"))).sendKeys(email);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='password']"))).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='phone']"))).sendKeys(phone);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='birthday']"))).sendKeys(birthday);

        genderForm();

        driver.findElement(By.xpath("//button[@type='submit' and normalize-space()='Đăng ký']")).click();
    }

    //lấy thông báo lỗi trên các trường input (name/email/password/phone) và thông báo khi thực hiện đăng ký(thành công / thất bại)
    public String getRegisterMessage() {
        try {
            WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//div[@class='ant-form-item-explain-error'] | //span[contains(.,'Đăng ký thành công') or contains(.,'Email đã tồn tại !')])")
            ));
            return messageElement.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    //mở form giới tính
    public void genderForm() {
        // bắt ô chọn giới tính
        By genderInput = By.xpath("//div[@id='gender' or @name='gender' or @role='combobox']");
        wait.until(ExpectedConditions.elementToBeClickable(genderInput)).click();

        // Đợi menu dropdown hiện ra
        By dropdownVisible = By.xpath("//div[contains(@class,'ant-select-dropdown') and not(contains(@class,'hidden'))]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownVisible));

        // Chọn giới tính "Nam"
        By genderOption = By.xpath("//div[@class='ant-select-item-option-content' and normalize-space()='Nam']");
        wait.until(ExpectedConditions.elementToBeClickable(genderOption)).click();
    }

    //lấy giá trị trong ô birthday
    public String getBirthdayValue() {
        WebElement birthdayInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("birthday")));
        return birthdayInput.getAttribute("value");
    }

    //chọn ngày sinh trên locator birthday
    public void selectBirthdayFromCalendar(String expectedDate) {
        openRegisterForm();

        WebElement birthdayInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("birthday")));
        birthdayInput.click();
        birthdayInput.sendKeys(expectedDate);
    }

    //chọn ngày sinh trong tương lai
    public void selectFutureBirthday(String futureDate) {
        openRegisterForm();

        WebElement birthdayInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("birthday")));
        birthdayInput.click();
        birthdayInput.sendKeys(futureDate);
        birthdayInput.sendKeys("\t");
    }
    // lấy thông báo lỗi từ ô birthday
    public String getBirthdayErrorMessage() {
        try {
            WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='ant-form-item-explain-error' and (contains(text(),'ngày sinh') or contains(text(),'không hợp lệ'))]")
            ));
            return errorElement.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }


}
