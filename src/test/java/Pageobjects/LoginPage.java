package Pageobjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BasePage {
    
    // ============ CORRECT LOCATORS FOR ORANGEHRM ============
    
    // Username field - using name attribute
    @FindBy(name = "username")
    private WebElement usernameField;
    
    // Password field - using name attribute
    @FindBy(name = "password")
    private WebElement passwordField;
    
    // Login button - using CSS selector
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    // Error message - using CSS selector for the alert
    @FindBy(css = ".oxd-alert-content--error")
    private WebElement errorMessage;
    
    // Dashboard header - to verify successful login
    @FindBy(css = ".oxd-topbar-header-breadcrumb-module")
    private WebElement dashboardHeader;
    
    // User dropdown for logout
    @FindBy(css = ".oxd-userdropdown-tab")
    private WebElement userDropdown;
    
    // Logout link
    @FindBy(linkText = "Logout")
    private WebElement logoutLink;
    
    // Constructor
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    // ============ ACTIONS ============
    
    public void enterUsername(String username) {
        waitForVisibility(usernameField);
        usernameField.clear();
        usernameField.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        waitForVisibility(passwordField);
        passwordField.clear();
        passwordField.sendKeys(password);
    }
    
    public void clickLoginButton() {
        waitForClickable(loginButton);
        loginButton.click();
    }
    
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
    
    public boolean isLoginSuccessful() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf(dashboardHeader));
            return dashboardHeader.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isLoginFailed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getErrorMessage() {
        try {
            waitForVisibility(errorMessage);
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public void logout() {
        try {
            waitForClickable(userDropdown);
            userDropdown.click();
            Thread.sleep(500);
            waitForClickable(logoutLink);
            logoutLink.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isLoginPageDisplayed() {
        try {
            return usernameField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}