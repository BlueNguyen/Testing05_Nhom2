package listener;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import scripts.BaseTest;
import utils.ScreenshotUtil;

public class SimpleListeners implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test Started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed: " + result.getName());

        Object currentClass = result.getInstance();
        WebDriver driver = ((BaseTest) currentClass).driver;

        String testName = result.getName();
        String screenshotPath = ScreenshotUtil.captureScreenshot(driver, testName);
        System.out.println("Screenshot saved to: " + screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test Skipped: " + result.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Start testing: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Finished testing: " + context.getName());
    }
}
