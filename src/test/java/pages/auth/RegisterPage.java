package pages.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
    public void openRegisterForm(){
        By registerIcon = By.xpath("//button[img[contains(@src,\"6596121.png\")]]");
        wait.until(ExpectedConditions.elementToBeClickable(registerIcon)).click();

        By registerBtn = By.xpath("//button[normalize-space()=\"Đăng ký\"][1]");
        wait.until(ExpectedConditions.elementToBeClickable(registerBtn)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id=\"name\"]")));
    }
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
    public void genderForm() {
        // Bắt ô chọn giới tính (Ant Design thường không phải input mà là div/combobox)
        By genderInput = By.xpath("//div[@id='gender' or @name='gender' or @role='combobox']");
        wait.until(ExpectedConditions.elementToBeClickable(genderInput)).click();

        // Đợi menu dropdown hiện ra (AntD thêm class 'ant-select-dropdown' khi mở)
        By dropdownVisible = By.xpath("//div[contains(@class,'ant-select-dropdown') and not(contains(@class,'hidden'))]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownVisible));

        // Chọn giới tính "Nam"
        By genderOption = By.xpath("//div[@class='ant-select-item-option-content' and normalize-space()='Nam']");
        wait.until(ExpectedConditions.elementToBeClickable(genderOption)).click();
    }



}
