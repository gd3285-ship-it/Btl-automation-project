package junitExtensions;

import driver.DriverFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WebDriverExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) {
        DriverFactory.initDriver();
        DriverFactory.getDriver().get("https://www.btl.gov.il/Pages/default.aspx");
        DriverFactory.getDriver().manage().window().maximize();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        DriverFactory.quitDriver();
    }
}