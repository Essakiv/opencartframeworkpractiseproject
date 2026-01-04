package Testbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import Utilities.ExcelUtilities;
import Utilities.ExtentReportManager;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {
    
    public static WebDriver driver;
    public static Properties prop;
    public static Logger logger;
    public static ExcelUtilities excelUtils;
    
    // Paths
    public static final String TEST_DATA_PATH = System.getProperty("user.dir") + "/Testdata/Book1.xlsx";
    public static final String CONFIG_PATH = System.getProperty("user.dir") + "/src/test/resources/Config.properties";
    public static final String SCREENSHOTS_PATH = System.getProperty("user.dir") + "/Screenshots/";
    
    @BeforeMethod(alwaysRun = true)
    @Parameters({"os", "browser"})
    public void setUp(@Optional("windows") String os, @Optional("chrome") String browser) throws MalformedURLException {
        // Initialize Logger
        logger = LogManager.getLogger(this.getClass());
        
        // Load Config Properties
        loadConfig();
        
        // Get execution environment from config
        String executionEnv = prop.getProperty("execution_env", "local");
        
        // Initialize WebDriver based on execution environment
        if (executionEnv.equalsIgnoreCase("remote")) {
            initializeRemoteDriver(os, browser);
            logger.info("Remote Browser launched on Selenium Grid: " + browser + " on " + os);
            ExtentReportManager.logInfo("Remote Browser launched: " + browser + " on " + os);
        } else {
            initializeLocalDriver(browser);
            logger.info("Local Browser launched: " + browser);
            ExtentReportManager.logInfo("Local Browser launched: " + browser);
        }
        
        // Browser settings
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
            Integer.parseInt(prop.getProperty("implicitWait", "10"))));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(
            Integer.parseInt(prop.getProperty("pageLoadTimeout", "30"))));
        driver.manage().deleteAllCookies();
        
        // Navigate to application URL
        String baseURL = prop.getProperty("baseURL");
        if (baseURL != null && !baseURL.isEmpty()) {
            driver.get(baseURL);
            logger.info("Navigated to: " + baseURL);
        }
        
        // Initialize Excel Utilities
        excelUtils = new ExcelUtilities(TEST_DATA_PATH);
    }
    
    public void loadConfig() {
        try {
            prop = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_PATH);
            prop.load(fis);
            fis.close();
            logger.info("Config file loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to load config file: " + e.getMessage());
        }
    }
    
    // ==================== REMOTE DRIVER (SELENIUM GRID) ====================
    public void initializeRemoteDriver(String os, String browser) throws MalformedURLException {
        
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        // Set Platform/OS
        switch (os.toLowerCase()) {
            case "windows":
                capabilities.setPlatform(Platform.WIN11);
                break;
            case "mac":
                capabilities.setPlatform(Platform.MAC);
                break;
            case "linux":
                capabilities.setPlatform(Platform.LINUX);
                break;
            default:
                capabilities.setPlatform(Platform.ANY);
                logger.warn("Unknown OS: " + os + ", using Platform.ANY");
        }
        
        // Get Grid URL from config
        String gridURL = prop.getProperty("grid_url", "http://localhost:4444/wd/hub");
        
        // Set Browser
        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.merge(capabilities);
                driver = new RemoteWebDriver(new URL(gridURL), chromeOptions);
                break;
                
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.merge(capabilities);
                driver = new RemoteWebDriver(new URL(gridURL), firefoxOptions);
                break;
                
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.merge(capabilities);
                driver = new RemoteWebDriver(new URL(gridURL), edgeOptions);
                break;
                
            default:
                ChromeOptions defaultOptions = new ChromeOptions();
                defaultOptions.addArguments("--remote-allow-origins=*");
                defaultOptions.merge(capabilities);
                driver = new RemoteWebDriver(new URL(gridURL), defaultOptions);
                logger.warn("Unknown browser: " + browser + ", using Chrome as default");
        }
        
        logger.info("Remote WebDriver initialized with Grid URL: " + gridURL);
    }
    
    // ==================== LOCAL DRIVER ====================
    public void initializeLocalDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
                
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions defaultOptions = new ChromeOptions();
                defaultOptions.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(defaultOptions);
                logger.warn("Unknown browser: " + browser + ", using Chrome as default");
        }
        
        logger.info("Local WebDriver initialized");
    }
    
    // Instance method for screenshot
    public String captureScreenshot(String testName) {
        return captureScreenshotStatic(testName);
    }
    
    // Static method for screenshot (used by ExtentReportManager)
    public static String captureScreenshotStatic(String testName) {
        if (driver == null) {
            return null;
        }
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = SCREENSHOTS_PATH + testName + "_" + timestamp + ".png";
        
        try {
            // Create Screenshots folder if not exists
            File screenshotDir = new File(SCREENSHOTS_PATH);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(screenshotPath);
            FileUtils.copyFile(source, destination);
            
            return screenshotPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed");
            ExtentReportManager.logInfo("Browser closed");
        }
    }
}