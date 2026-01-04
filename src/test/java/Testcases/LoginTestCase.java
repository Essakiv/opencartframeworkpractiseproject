package Testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

import Pageobjects.LoginPage;
import Testbase.BaseClass;
import Utilities.ExtentReportManager;

public class LoginTestCase extends BaseClass {
    
    private LoginPage loginPage;
    private static final String SHEET_NAME = "Sheet1";
    
    @Test(priority = 1, groups = {"smoke", "regression", "login"}, 
          description = "Verify valid user can login successfully")
    public void testValidLogin() {
        ExtentReportManager.logInfo("Starting Valid Login Test");
        
        String baseUrl = prop.getProperty("baseURL");
        driver.get(baseUrl);
        logger.info("Navigated to: " + baseUrl);
        ExtentReportManager.logInfo("Navigated to: " + baseUrl);
        
        loginPage = new LoginPage(driver);
        
        String username = excelUtils.getCellData(SHEET_NAME, "Username", 1);
        String password = excelUtils.getCellData(SHEET_NAME, "Password", 1);
        
        ExtentReportManager.logInfo("Username: " + username);
        
        if (username == null || username.isEmpty()) {
            username = "Admin";
            password = "[REDACTED:PASSWORD]";
            ExtentReportManager.logWarning("Using default credentials");
        }
        
        loginPage.login(username, password);
        
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        if (loginPage.isLoginSuccessful()) {
            logger.info("✓ Login successful for: " + username);
            ExtentReportManager.logPass("Login successful for: " + username);
            
            // Take screenshot of successful login
            String screenshotPath = captureScreenshot("ValidLogin_Success");
            ExtentReportManager.logScreenshot(screenshotPath, "Login Success");
            
            Assert.assertTrue(true);
            loginPage.logout();
        } else {
            logger.error("✗ Login failed for: " + username);
            ExtentReportManager.logFail("Login failed for: " + username);
            Assert.assertTrue(driver.getCurrentUrl().contains("account"),
                    "Login should be successful");

        }
    }
    
    @Test(priority = 2, groups = {"smoke", "regression", "login"}, 
          description = "Verify invalid user cannot login")
    public void testInvalidLogin() {
        ExtentReportManager.logInfo("Starting Invalid Login Test");
        
        String baseUrl = prop.getProperty("baseURL");
        driver.get(baseUrl);
        logger.info("Navigated to: " + baseUrl);
        ExtentReportManager.logInfo("Navigated to: " + baseUrl);
        
        loginPage = new LoginPage(driver);
        
        String username = excelUtils.getCellData(SHEET_NAME, "Username", 2);
        String password = excelUtils.getCellData(SHEET_NAME, "Password", 2);
        
        ExtentReportManager.logInfo("Username: " + username);
        
        if (username == null || username.isEmpty()) {
            username = "InvalidUser";
            password = "[REDACTED:PASSWORD]";
        }
        
        loginPage.login(username, password);
        
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        if (loginPage.isLoginFailed()) {
            logger.info("✓ Login failed as expected for: " + username);
            ExtentReportManager.logPass("Login failed as expected for: " + username);
            Assert.assertTrue(true);
        } else {
            logger.error("✗ Login passed but should have failed");
            ExtentReportManager.logFail("Login passed but should have failed");
            Assert.fail("Login should fail with invalid credentials");
        }
    }
    
    @Test(priority = 3, groups = {"regression"}, 
          description = "Verify login with empty credentials")
    public void testEmptyCredentials() {
        ExtentReportManager.logInfo("Starting Empty Credentials Test");
        
        String baseUrl = prop.getProperty("baseURL");
        driver.get(baseUrl);
        ExtentReportManager.logInfo("Navigated to: " + baseUrl);
        
        loginPage = new LoginPage(driver);
        
        loginPage.login("", "");
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        
        // Check for validation message
        Assert.assertTrue(loginPage.isLoginFailed() || !loginPage.isLoginSuccessful(), 
                "Login should fail with empty credentials");
        ExtentReportManager.logPass("Empty credentials handled correctly");
    }
    
    @Test(priority = 4, groups = {"sanity"}, 
          description = "Verify login page is displayed")
    public void testLoginPageDisplayed() {
        ExtentReportManager.logInfo("Verifying Login Page Display");
        
        String baseUrl = prop.getProperty("baseURL");
        driver.get(baseUrl);
        
        loginPage = new LoginPage(driver);
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("login"), "Login page should be displayed");
        ExtentReportManager.logPass("Login page displayed correctly");
        
        // Take screenshot
        String screenshotPath = captureScreenshot("LoginPage");
        ExtentReportManager.logScreenshot(screenshotPath, "Login Page");
    }
}