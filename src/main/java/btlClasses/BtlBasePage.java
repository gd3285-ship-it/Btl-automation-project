package btlClasses;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BtlBasePage extends BasePage {

    public BtlBasePage(WebDriver driver) {
        super(driver);
    }


    //לחיצה על רכיב מהתפריט הראשי
    public void ClickMenu(Menu menu) {
        driver.findElement(By.id(menu.getId())).click();
    }


    // לחיצה על רכיב מתת-התפריט
    public void ClickSubMenu(String subMenu) {
        driver.findElement(By.xpath("//a[normalize-space(text())='" + subMenu + "']")).click();
    }


    // חיפוש
    public void Search(String text) {
        WebElement inputSearch = driver.findElement(By.id("TopQuestions"));
        inputSearch.click();
        inputSearch.sendKeys(text);
        driver.findElement(By.id("ctl00_SiteHeader_reserve_btnSearch")).click();
    }

    public BranchesPage Branches() {
        driver.findElement(By.id("ctl00_Topmneu_BranchesHyperLink")).click();
        return new BranchesPage(driver);
    }
}