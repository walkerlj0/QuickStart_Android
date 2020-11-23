package tests;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import static tests.Config.host;
//import static tests.Config.region;

// Created from the SwagEmuAndroidTest by Eyal Yoval: https://github.com/eyaly/SauceAppiumSample
public class Mobile_Native_Local_Test {
    private static ThreadLocal<AndroidDriver> androidDriver = new ThreadLocal<AndroidDriver>();// help. why?
    private  ThreadLocal<String> sessionId = new ThreadLocal<>(); //what value is set here?

    String usernameID = "test-Username";
    String passwordID = "test-Password";
    String submitButtonID = "test-LOGIN";
    By ProductTitle = By.xpath("//android.widget.TextView[@text='PRODUCTS']");


    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {
        System.out.println("Sauce Android Native - BeforeMethod hook");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String appName = "Android.SauceLabs.Mobile.Sample.app.2.7.0.apk";
        URL serverUrl;
        capabilities.setCapability("deviceName", "Android Emulator"); //This will change to type of device e.g. Pixel 4 on Saucelabs
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion","9.0" ); //add platformVersion
        capabilities.setCapability("app", "/Users/lindsaywalker/Documents/Example_Tests/" + appName);
        serverUrl = new URL("http://localhost:4723/wd/hub"); //Is this correct?

        capabilities.setCapability("appActivity", "com.swaglabsmobileapp.MainActivity"); //Is this needed?
        capabilities.setCapability("appWaitActivity", "com.swaglabsmobileapp.MainActivity");
        capabilities.setCapability("automationName", "UiAutomator2"); //Why not Espresso? How do I know?

        try {
            androidDriver.set(new AndroidDriver(serverUrl, capabilities));
        } catch (Exception e) {
            System.out.println("*** Problem creating the Android driver " + e.getMessage());
            throw new RuntimeException(e);
        }// Can get rid of it for simplicity.
        // Old String id = ((RemoteWebDriver) getAndroidDriver()).getSessionId().toString();
        String id = getAndroidDriver().getSessionId().toString(); //Is this correct? Is this used?
        sessionId.set(id);
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        System.out.println("Sauce - AfterMethod hook");
        try {
            if (host.equals("saucelabs")) {
                ((JavascriptExecutor) getAndroidDriver()).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
            }
        } finally {
            System.out.println("Sauce - release driver");
            getAndroidDriver().quit();
        }
    }

    public  AndroidDriver getAndroidDriver() {
        return androidDriver.get();
    }

    @Test
    public void loginToSwagLabsTestValid() {
        System.out.println("Sauce - Start loginToSwagLabsTestValid test");

        login("standard_user", "secret_sauce");

        // Verificsation
        Assert.assertTrue(isOnProductsPage());
    }

    @Test
    public void loginTestValidProblem() {
        System.out.println("Sauce - Start loginTestValidProblem test");

        login("problem_user", "secret_sauce");

        // Verificsation - we on Product page
        Assert.assertTrue(isOnProductsPage());
    }

    public void login(String user, String pass){
        AndroidDriver driver = getAndroidDriver();

        WebDriverWait wait = new WebDriverWait(driver, 15);
        final WebElement usernameEdit = wait.until(ExpectedConditions.visibilityOfElementLocated(new MobileBy.ByAccessibilityId(usernameID)));

        //WebElement usernameEdit = (WebElement) driver.findElementByAccessibilityId(usernameID);
        usernameEdit.click();
        usernameEdit.sendKeys(user);

        WebElement passwordEdit = (WebElement) driver.findElementByAccessibilityId(passwordID);
        passwordEdit.click();
        passwordEdit.sendKeys(pass);

        WebElement submitButton = (WebElement) driver.findElementByAccessibilityId(submitButtonID);
        submitButton.click();
    }

    public boolean isOnProductsPage() {
        AndroidDriver driver = getAndroidDriver();
        //Create an instance of a Selenium explicit wait so that we can dynamically wait for an element
        WebDriverWait wait = new WebDriverWait(driver, 5);

        //wait for the product field to be visible and store that element into a variable
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ProductTitle));
        } catch (TimeoutException e){
            return false;
        }
        return true;
    }
}
