package listener;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentManager;
import utils.ScreenshotUtil;

public class ExtentReportListener implements ITestListener {

    // Khởi tạo ExtentReports thông qua ExtentManager
    public static ExtentReports extent;

    // ThreadLocal để tránh xung đột khi chạy test song song
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        String testName = context.getName(); // từ TestNG XML <test name="...">
        String reportFileName = testName + "_Report.html";
        extent = ExtentManager.createInstance(reportFileName);
    }

    // Khi test bắt đầu
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    // Khi test pass
    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "✅ Test Passed");
    }

    // Khi test fail
    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "❌ Test Failed: " + result.getThrowable());

        WebDriver driver = (WebDriver) result.getTestContext().getAttribute("driver");
        String methodName = result.getMethod().getMethodName();

        if (driver != null) {
            // Nhận đường dẫn ảnh sau khi chụp
            String screenshotPath = ScreenshotUtil.captureScreenshot(driver, methodName);
            if (screenshotPath != null) {
                try {
                    test.get().addScreenCaptureFromPath(screenshotPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Khi test bị bỏ qua (skip)
    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "⚠️ Test Skipped");
    }

    // Khi toàn bộ test của một suite/class kết thúc
    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    // Trả về test hiện tại, dùng nếu muốn log ở nơi khác
    public static ExtentTest getTest() {
        return test.get();
    }
}
