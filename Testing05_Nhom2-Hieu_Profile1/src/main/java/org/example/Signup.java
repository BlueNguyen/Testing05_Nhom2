package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Signup {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            driver.manage().window().maximize();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Mở trang
            driver.get("https://demo4.cybersoft.edu.vn/");
            System.out.println("Đã mở trang thành công");

            // Mở menu người dùng
            WebElement userIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class,'bg-main')]/img[contains(@src,'6596/6596121')]")
            ));
            js.executeScript("arguments[0].click();", userIcon);
            System.out.println("Đã mở menu người dùng");

            // 🔹 Mở form Đăng ký
            WebElement signUpBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Đăng ký')]")
            ));
            js.executeScript("arguments[0].click();", signUpBtn);
            System.out.println("Đã mở form đăng ký");

            // 🔹 Điền thông tin form đăng ký
            driver.findElement(By.name("name")).sendKeys("Hieu");
            //String email = "hieu002@gmail.com";
            String email = "test" + System.currentTimeMillis() + "@gmail.com";
            driver.findElement(By.name("email")).sendKeys(email);
            //driver.findElement(By.name("password")).sendKeys("blue299");
            driver.findElement(By.name("password")).sendKeys("123456");
            driver.findElement(By.name("phone")).sendKeys("0987654321");

            //Ngày sinh (tùy chọn)
            List<WebElement> birthday = driver.findElements(By.name("birthday"));
            if (!birthday.isEmpty()) {
                birthday.get(0).click();
                Thread.sleep(1000);

                // Chọn ngày 15 nếu có
                List<WebElement> day = driver.findElements(By.xpath("//div[contains(@class,'ant-picker-cell-inner') and text()='15']"));
                if (!day.isEmpty()) {
                    day.get(0).click();
                }
                System.out.println("Đã chọn ngày sinh");
            } else {
                System.out.println("Bỏ qua ngày sinh (không có trường này)");
            }

            // Giới tính (tùy chọn)
            List<WebElement> gender = driver.findElements(By.xpath("//div[contains(@class,'ant-select-selector')]"));
            if (!gender.isEmpty()) {
                gender.get(0).click();
                Thread.sleep(500);
                List<WebElement> options = driver.findElements(By.xpath("//div[@class='ant-select-item-option-content' and (text()='Nam' or text()='Male')]"));
                if (!options.isEmpty()) {
                    options.get(0).click();
                }
                System.out.println("Đã chọn giới tính");
            } else {
                System.out.println("Bỏ qua giới tính (không có trường này)");
            }

            // 🔹 Nhấn nút “Đăng ký”
            WebElement registerBtn = driver.findElement(
                    By.xpath("//div[contains(@class,'ant-modal-content')]//button[contains(text(),'Đăng ký')]")
            );
            js.executeScript("arguments[0].click();", registerBtn);
            System.out.println("Đã gửi form đăng ký");
            Thread.sleep(4000); // cho site xử lý đăng ký xong

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi chạy test: " + e.getMessage());
        } finally {
            // driver.quit(); // giữ lại trình duyệt để xem kết quả
        }
    }
}
