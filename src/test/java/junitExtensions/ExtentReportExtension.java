package junitExtensions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import driver.DriverFactory;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import reports.ExtentReportManager;
import reports.ExtentTestManager;
import utils.ScreenshotUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private static ExtentTest extentTest;

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        extentTest = ExtentReportManager.getExtent().createTest(testName);
        ExtentTestManager.setTest(extentTest);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (context.getExecutionException().isPresent()) {
            String cause = context.getExecutionException().get().getMessage();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            String screenshotPath = "./target/ExtentReport/screenshots/" + context.getRequiredTestMethod().getName() + timestamp + ".png";

            String relativePath = "./screenshots/" + context.getRequiredTestMethod().getName() + timestamp + ".png";

            boolean isTaken = ScreenshotUtils.takeScreenshot(DriverFactory.getDriver(), screenshotPath);

            if (isTaken) {
                extentTest.fail(cause, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            } else {
                extentTest.fail("Test failed, but screenshot could not be taken.");
                extentTest.fail(cause);
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        ExtentReportManager.getExtent().flush();
    }
}