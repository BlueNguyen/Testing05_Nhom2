package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    public static String captureScreenshot(WebDriver driver, String namePrefix) {
        // Kiểm tra driver có hỗ trợ chụp màn hình không
        if (!(driver instanceof TakesScreenshot)) {
            System.out.println("⚠️ Driver không hỗ trợ chụp màn hình");
            return null;
        }

        // Chụp màn hình tạm
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // Chuỗi thời gian cho tên file (ví dụ: 20251021_183245)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Thư mục và file đích
        File destDir = new File("reports/screenshots");
        File destFile = new File(destDir, namePrefix + "_" + timeStamp + ".png");

        try {
            Files.createDirectories(destDir.toPath());
            Files.copy(srcFile.toPath(), destFile.toPath());

            System.out.println("✅ Đã chụp màn hình: " + destFile.getAbsolutePath());

            // Trả về đường dẫn tương đối (phục vụ cho Extent Report)
            return "screenshots/" + destFile.getName();

        } catch (IOException e) {
            System.out.println("❌ Lỗi lưu ảnh: " + e.getMessage());
            return null;
        }
    }
}
