package Pageobjects;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }
    
    protected void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void waitForClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected void click(WebElement element) {
        waitForClickable(element);
        element.click();
    }
    
    protected void enterText(WebElement element, String text) {
        waitForVisibility(element);
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getText(WebElement element) {
        waitForVisibility(element);
        return element.getText();
    }
    
    protected boolean isDisplayed(WebElement element) {
        try {
            waitForVisibility(element);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}