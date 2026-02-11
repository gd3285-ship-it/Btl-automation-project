package tests;

import btlClasses.BranchesPage;
import btlClasses.BtlBasePage;
import btlClasses.HomePage;
import btlClasses.Menu;
import com.aventstack.extentreports.ExtentTest;
import driver.DriverFactory;
import junitExtensions.ExtentReportExtension;
import junitExtensions.WebDriverExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import reports.ExtentTestManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@ExtendWith({ExtentReportExtension.class, WebDriverExtension.class})
public class BtlTest {

    WebDriver driver;
    ExtentTest test;
    WebDriverWait wait;

    void InitTest() {
        driver = DriverFactory.getDriver();
        test = ExtentTestManager.getTest();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }


    @Test
    public void SearchMaternityPayAmount() {

        InitTest();

        test.info("מתחיל טסט חיפוש: חישוב סכום דמי לידה");
        HomePage homePage = new HomePage(driver);

        String searchText = "חישוב סכום דמי לידה ליום";
        homePage.Search(searchText);

        test.pass("בוצע חיפוש בהצלחה");

        String expectedTitle = "תוצאות חיפוש עבור " + searchText;
        wait.until(ExpectedConditions.textToBe(By.cssSelector("#results h2"), expectedTitle));

        String searchTitle = driver.findElement(By.cssSelector("#results h2")).getText();
        Assertions.assertEquals(expectedTitle, searchTitle, "Search page title not match");

        test.pass("הכותרת תואמת לדרישה");
    }


    @Test
    public void EnterToBranchesPage() {

        InitTest();

        test.info("מתחיל טסט: כניסה לעמוד סניפים");

        driver.findElement(By.xpath("//*[@id=\"ctl00_Topmneu_BranchesHyperLink\"]")).click();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".opener-bg h1")));
        Assertions.assertEquals("סניפים וערוצי שירות", title.getText(), "The page that opens is not BRANCHES AND SERVICE CHANNELS");

        test.pass("הכותרת תואמת לציפיות");

        driver.findElements(By.className("SnifName")).get(0).click();

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("label")));
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));

        Assertions.assertEquals("כתובת", labels.get(1).getText(), "Address not appear on BRANCH page");
        Assertions.assertEquals("קבלת קהל", labels.get(2).getText(), "Reception not appear on BRANCH page");
        Assertions.assertEquals("מענה טלפוני", labels.get(3).getText(), "Telephone-answering not appear on BRANCH page");

        test.pass("השדות הנדרשים מופיעים");
    }


    @Test
    public void PaymentsForYeshivaStudent() {

        InitTest();

        test.info("מתחיל טסט: חישוב תשלום לבן ישיבה");

        BtlBasePage btlBasePage = new BtlBasePage(driver);
        btlBasePage.ClickMenu(Menu.INSURANCE);
        btlBasePage.ClickSubMenu("דמי ביטוח לאומי");

        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lbl_title")));
        Assertions.assertEquals("דמי ביטוח לאומי", titleElement.getText(),
                "The page that opens is not NATIONAL INSURANCE CONTRIBUTIONS");

        test.pass("כותרת הדף תואמת לציפיות");

        driver.findElement(By.xpath("//a[.//strong[normalize-space(text())='מחשבון לחישוב דמי הביטוח']]")).click();

        WebElement h1Title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
        Assertions.assertEquals("חישוב דמי ביטוח עבור עצמאי, תלמיד, שוהה בחוץ לארץ ומי שלא עובד", h1Title.getText(),
                "The page that opens is not INSURANCE PREMIUM CALCULATION");

        test.pass("כותרת הדף תואמת לציפיות");

        driver.findElement(By.xpath("//label[text()='תלמיד ישיבה']")).click();
        driver.findElement(By.xpath("//input[@type='radio' and following-sibling::label[contains(text(),'זכר')]]")).click();

        LocalDate today = LocalDate.now();
        long minDay = today.minusYears(70).toEpochDay();
        long maxDay = today.minusYears(18).minusDays(1).toEpochDay();
        driver.findElement(By.xpath("//input[contains(@id,'BirthDate_Date')]"))
                .sendKeys(LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(minDay, maxDay + 1))
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        driver.findElement(By.xpath("//input[contains(@id,'StartNextButton')]")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("header"), "צעד שני"));

        String stepTitle = driver.findElement(By.id("header")).getText();
        Assertions.assertTrue(stepTitle.contains("צעד שני"), "The page that opens is not STEP TWO");

        test.pass("הטסט הגיע לצעד שני");

        WebElement radio = driver.findElement(By.xpath("//input[@type='radio' and @value='False']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);

        WebElement nextButton = driver.findElement(By.xpath("//input[contains(@id,'StepNextButton')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("header"), "סיום"));

        String finalTitle = driver.findElement(By.id("header")).getText();
        Assertions.assertTrue(finalTitle.contains("סיום"), "The page that opens is not FINAL");

        test.pass("הטסט הגיע לשלב הסיום");

        double nationalInsurance = Double.parseDouble(
                driver.findElement(By.xpath("//li[contains(text(), 'דמי ביטוח לאומי')]//strong")).getText()
        );
        double healthInsurance = Double.parseDouble(
                driver.findElement(By.xpath("//li[contains(text(), 'דמי ביטוח בריאות')]//strong")).getText()
        );
        double totalInsurance = Double.parseDouble(
                driver.findElement(By.xpath("//li[contains(text(), 'סך הכל דמי ביטוח')]//strong")).getText()
        );

        Assertions.assertEquals(nationalInsurance + healthInsurance, totalInsurance, "The insurance premium amount is incorrect!");

        test.pass("תוצאות החישובים נכונות");
    }


    @Test
    public void CalculateNationalInsuranceContributions() {

        InitTest();

        test.info("מתחיל טסט: חישוב דמי ביטוח לאומי");

        BtlBasePage btlBasePage = new BtlBasePage(driver);
        btlBasePage.ClickMenu(Menu.BENEFITS);
        btlBasePage.ClickSubMenu("אבטלה");
        WebElement calc = driver.findElement(By.xpath("//a[.//strong[normalize-space(text())='למחשבוני דמי אבטלה']]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", calc);

        driver.findElement(By.xpath("//a[.//text()='חישוב דמי אבטלה']")).click();

        WebElement inputDate = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@id,'PiturimDate_Date')]")));
        LocalDate date = LocalDate.now().minusMonths(1);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        inputDate.click();
        inputDate.sendKeys(formattedDate);
        driver.findElement(By.xpath("//input[contains(@id,'AvtalaWizard_rdb_age_1')]")).click();

        driver.findElement(By.xpath("//input[@class='btnNext' and @value='המשך']")).click();

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("//table//input[contains(@class, 'txtbox_sallary')]")
        ));

        List<WebElement> EarningsAmounts = driver.findElements(By.xpath("//table//input[contains(@class, 'txtbox_sallary')]"));

        Random random = new Random();
        for (WebElement input : EarningsAmounts) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", input);
            input.clear();
            input.sendKeys(String.valueOf(random.nextInt(9001)));
        }

        WebElement nextBtn = driver.findElement(By.xpath("//input[@class='btnNext' and @value='המשך']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextBtn);
        nextBtn.click();

        WebElement titleElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3.Question_gray.bld.txt13")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", titleElement);

        Assertions.assertEquals("תוצאות החישוב", titleElement.getText().trim(), "The page that opens is not THE CALCULATION RESULTS");

        test.pass("כותרת הדף תואמת לציפיות");

        String[] titles = {"שכר יומי ממוצע לצורך חישוב דמי אבטלה", "דמי אבטלה ליום", "דמי אבטלה לחודש"};

        List<WebElement> resultItems = driver.findElements(By.xpath(
                "//li[" +
                        "contains(normalize-space(.), '" + titles[0] + "') or " +
                        "contains(normalize-space(.), '" + titles[1] + "') or " +
                        "contains(normalize-space(.), '" + titles[2] + "')" +
                        "]//label"
        ));

        for (int i = 0; i < titles.length; i++)
            Assertions.assertTrue(resultItems.get(i).getText().contains(titles[i]),
                    "Expected title to contain " + titles[i] + " but was:" + resultItems.get(i).getText());

        test.pass("הכותרות תואמות לציפיות");
    }


    @ParameterizedTest()
    @CsvSource({
            "קצבת ילדים, דף הבית/קצבאות והטבות/ילדים",
            "דמי לידה, דף הבית/קצבאות והטבות/אמהות/דמי לידה",
            "ניידות, דף הבית/קצבאות והטבות/ניידות",
            "אסירי ציון, דף הבית/קצבאות והטבות/אסירי ציון והרוגי מלכות",
            "מילואים, דף הבית/קצבאות והטבות/מילואים"
    })
    public void PageFromAllowancesAndBenefitsByPARAMETRIZE(String benefitName, String expectedBreadcrumb) {

        InitTest();

        test.info("בודק ניווט עבור: " + benefitName);

        BtlBasePage btlPage = new BtlBasePage(driver);
        btlPage.ClickMenu(Menu.BENEFITS);
        btlPage.ClickSubMenu(benefitName);

        String[] breadcrumbParts = expectedBreadcrumb.split("/");
        String lastExpectedPart = breadcrumbParts[breadcrumbParts.length - 1];

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".breadcrumbs-item:last-child"), lastExpectedPart));

        List<WebElement> breadcrumbLinks = driver.findElements(By.cssSelector(".breadcrumbs-item"));

        String actualBreadcrumb = breadcrumbLinks.stream()
                .map(WebElement::getText)
                .map(text -> text.replace("\n", "").trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.joining("/"));

        Assertions.assertEquals(expectedBreadcrumb, actualBreadcrumb);
        test.pass("נתיב פירורי הלחם תקין: " + actualBreadcrumb);
    }
}