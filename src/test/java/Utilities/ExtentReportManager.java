package Utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import Testbase.BaseClass;

public class ExtentReportManager implements ITestListener {
    
    public static ExtentReports extent;
    public static ExtentTest test;
    public static String reportPath;
    
    // Called when test suite starts
    @Override
    public void onStart(ITestContext context) {
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        reportPath = System.getProperty("user.dir") + "/Reports/TestReport_" + timestamp + ".html";
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        
        // Report Configuration
        sparkReporter.config().setDocumentTitle("Automation Test Report");
        sparkReporter.config().setReportName("OrangeHRM Test Execution Report");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // System Information
        extent.setSystemInfo("Application", "OrangeHRM");
        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Browser", context.getCurrentXmlTest().getParameter("browser"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }
    
    // Called when each test starts
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        
        test = extent.createTest(testName);
        test.info("Test Started: " + testName);
        
        if (description != null && !description.isEmpty()) {
            test.info("Description: " + description);
        }
        
        // Log groups
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            test.assignCategory(groups);
        }
    }
    
    // Called when test passes
    @Override
    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "✓ Test PASSED: " + result.getMethod().getMethodName());
        test.log(Status.INFO, "Execution Time: " + getExecutionTime(result) + " seconds");
    }
    
    // Called when test fails
    @Override
    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "✗ Test FAILED: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, "Failure Reason: " + result.getThrowable().getMessage());
        
        // Capture screenshot on failure
        try {
            String screenshotPath = BaseClass.captureScreenshotStatic(result.getMethod().getMethodName());
            if (screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            }
        } catch (Exception e) {
            test.log(Status.WARNING, "Could not capture screenshot: " + e.getMessage());
        }
        
        test.log(Status.INFO, "Execution Time: " + getExecutionTime(result) + " seconds");
    }
    
    // Called when test is skipped
    @Override
    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "⚠ Test SKIPPED: " + result.getMethod().getMethodName());
        
        if (result.getThrowable() != null) {
            test.log(Status.SKIP, "Skip Reason: " + result.getThrowable().getMessage());
        }
    }
    
    // Called when test suite finishes
    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        
        // Automatically open report in browser
        try {
            File reportFile = new File(reportPath);
            if (reportFile.exists()) {
                Desktop.getDesktop().browse(reportFile.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method to calculate execution time
    private String getExecutionTime(ITestResult result) {
        long time = result.getEndMillis() - result.getStartMillis();
        return String.format("%.2f", time / 1000.0);
    }
    
    // Static methods for manual logging in test classes
    public static void logInfo(String message) {
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }
    
    public static void logPass(String message) {
        if (test != null) {
            test.log(Status.PASS, message);
        }
    }
    
    public static void logFail(String message) {
        if (test != null) {
            test.log(Status.FAIL, message);
        }
    }
    
    public static void logWarning(String message) {
        if (test != null) {
            test.log(Status.WARNING, message);
        }
    }
    
    public static void logScreenshot(String screenshotPath, String title) {
        if (test != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath, title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}