package training.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.io.File;

public class Test2_ff_nightly {
    public WebDriver driver;
    public WebDriverWait wait;

    private final Logger log = LoggerFactory.getLogger(Test1.class);
    int sleepTime;
    String browser;

    @Before
    public void start() {
        log.debug("start function started");

        String sleepTimeStr = System.getProperty("sleep-time");
        log.debug("sleep-time property: '" + sleepTimeStr + "'");
        if (sleepTimeStr == null || sleepTimeStr.isEmpty())
            sleepTime = 100;
        else
            sleepTime = Integer.parseInt(sleepTimeStr);

        log.info("Firefox browser start");
        boolean legacy = false;
        // boolean legacy = true;
        FirefoxOptions options = new FirefoxOptions()
            .setLegacy(legacy)
            .addPreference("unexpectedAlertBehaviour", "dismiss")
            .setBinary(new FirefoxBinary(new File("/data/bin/firefoxNightly/firefox")));
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        log.info("Options: " + ((HasCapabilities)driver).getCapabilities());

        wait = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        log.debug("start function finished");
    }

    @Test
    public void test1() {
        log.debug("test2 started");

        driver.navigate().to("http://localhost/litecart/admin");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
        wait.until(ExpectedConditions.titleIs("My Store"));

        log.debug("test2 finidhed");
    }

    @After
    public void stop() throws InterruptedException {
        log.debug("stop function started");

        Thread.sleep(sleepTime);
        driver.quit();
        log.debug("driver quitted");
        driver = null;

        log.debug("stop function finished");
    }
}
