package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ScreenshotUtils {

    public static boolean takeScreenshot(WebDriver driver, String destinationPath) {

        if (driver != null && destinationPath != null) {

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            try {
                File finalDestination = new File(destinationPath);
                finalDestination.getParentFile().mkdirs();
                Files.copy(srcFile.toPath(), finalDestination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                System.out.println("Failed to save screenshot: " + e.getMessage());
            }
        }

        System.out.println("Driver is null, so can't make screenshot");
        return false;
    }
}