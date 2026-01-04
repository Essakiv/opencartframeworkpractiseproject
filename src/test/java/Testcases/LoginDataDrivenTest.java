package Testcases;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Pageobjects.LoginPage;
import Testbase.BaseClass;
import Utilities.DataProviderClass;
import Utilities.ExcelUtilities;
import Utilities.ExtentReportManager;

public class LoginDataDrivenTest extends BaseClass {
    
    private LoginPage loginPage;
    private static final String SHEET_NAME = "Sheet1";
    
    @DataProvider(name = "LoginDataLocal")
    public Object[][] getLoginData() {
        ExcelUtilities excel = new ExcelUtilities(TEST_DATA_PATH);
        return excel.getSheetData(SHEET_NAME);
    }
    
    @Test(dataProvider = "LoginDataLocal", priority = 1, 
          groups = {"regression", "datadriven"},
          description = "Data Driven Login Test with Local DataProvider")
    public void testLoginWithLocalDataProvider(String username, String password, String expectedResult) {
        ExtentReportManager.logInfo("Testing with Username: " + username + ", Expected: " + expectedResult);
        
        String baseUrl = prop.getProperty("baseURL");
        driver.get(baseUrl);
        logger.info("Navigated to: " + baseUrl);
        
        loginPage = new LoginPage(driver);
        
        loginPage.login(username, password);
        
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        if (expectedResult.equalsIgnoreCase("Pass")) {
            if (loginPage.isLoginSuccessful()) {
                ExtentReportManager.logPass("Login successful for: " + username);
                Assert.assertTrue(true);
                loginPage.logout();
                try { Thread.sleep(2000); } catch (InterruptedException e) { }
            } else {
                ExtentReportManager.logFail("Login failed but expected to pass for: " + username);
                Assert.fail("Login should be successful for user: " + username);
            }
        } else {
            if (loginPage.isLoginFailed()) {
                ExtentReportManager.logPass("Login failed as expected for: " + username);
                Assert.assertTrue(true);
            } else {
                ExtentReportManager.logFail("Login passed but expected to fail for: " + username);
                Assert.fail("Login should fail for user: " + username);
            }
        }
    }
    
    @Test(dataProvider = "LoginData", dataProviderClass = DataProviderClass.class, 
          priority = 2, groups = {"regression", "datadriven"},
          description = "Data Driven Login Test with External DataProvider")
    public void testLoginWithExternalDataProvider(String username, String password, String expectedResult) {
        ExtentReportManager.logInfo("Testing with Username: " + username);
        
        String baseUrl = prop.getProperty("baseURL");
        driver.get(baseUrl);
        
        loginPage = new LoginPage(driver);
        
        loginPage.login(username, password);
        
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        if (expectedResult.equalsIgnoreCase("Pass")) {
            Assert.assertTrue(loginPage.isLoginSuccessful(), "Login should pass");
            ExtentReportManager.logPass("Login successful for: " + username);
            loginPage.logout();
        } else {
            Assert.assertTrue(loginPage.isLoginFailed(), "Login should fail");
            ExtentReportManager.logPass("Login failed as expected for: " + username);
        }
    }
    
    @Test(priority = 3, groups = {"regression", "datadriven", "excel"},
          description = "Login Test with Excel Result Update")
    public void testLoginAndUpdateResult() {
        ExtentReportManager.logInfo("Starting Excel Result Update Test");
        
        ExcelUtilities excel = new ExcelUtilities(TEST_DATA_PATH);
        int rowCount = excel.getRowCount(SHEET_NAME);
        
        String baseUrl = prop.getProperty("baseURL");
        
        int passCount = 0;
        int failCount = 0;
        
        for (int i = 1; i < rowCount; i++) {
            driver.get(baseUrl);
            
            try { Thread.sleep(2000); } catch (InterruptedException e) { }
            
            loginPage = new LoginPage(driver);
            
            String username = excel.getCellData(SHEET_NAME, "Username", i);
            String password = excel.getCellData(SHEET_NAME, "Password", i);
            String expectedResult = excel.getCellData(SHEET_NAME, "Result", i);
            
            ExtentReportManager.logInfo("Row " + i + ": Testing with Username: " + username);
            
            if (username == null || username.isEmpty()) {
                continue;
            }
            
            loginPage.login(username, password);
            
            try { Thread.sleep(3000); } catch (InterruptedException e) { }
            
            String actualResult;
            if (loginPage.isLoginSuccessful()) {
                actualResult = "Pass";
                loginPage.logout();
                try { Thread.sleep(2000); } catch (InterruptedException e) { }
            } else {
                actualResult = "Fail";
            }
            
            if (actualResult.equalsIgnoreCase(expectedResult)) {
                excel.setCellData(SHEET_NAME, i, 3, "PASSED");
                ExtentReportManager.logPass("Row " + i + ": PASSED for " + username);
                passCount++;
            } else {
                excel.setCellData(SHEET_NAME, i, 3, "FAILED");
                ExtentReportManager.logFail("Row " + i + ": FAILED for " + username);
                failCount++;
            }
        }
        
        ExtentReportManager.logInfo("Total Passed: " + passCount + ", Total Failed: " + failCount);
    }
}