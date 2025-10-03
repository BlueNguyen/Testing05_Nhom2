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
    public static String captureScreenshot(WebDriver driver, String namePrefix){
        //kiểm tra xem driver có hỗ trợ không
        if(!(driver instanceof TakesScreenshot)){
            System.out.println("Driver không hỗ trợ chụp màn hình");
        }

        //thực hiện thao tác chụp mành hình và lưu mục ảo
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        //Tạo chuỗi thời gian
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        20250713_091230
        //Tạo nơi lưu file hình ảnh
        File destDir = new File("reports/screenshots");
        File destFile = new File(destDir, namePrefix + "_" + timeStamp + ".png");

        try{
            Files.createDirectories(destDir.toPath());
            Files.copy(srcFile.toPath(), destFile.toPath());

            System.out.println("Đã chụp màn hình: " + destFile.getAbsolutePath());

            // Trả về đường dẫn tương đối tính từ ExtentReport.html
            return "screenshots/" + destFile.getName();
        } catch (IOException e) {
            System.out.println("Loi luu anh");
            return null;
        }
    }
}
