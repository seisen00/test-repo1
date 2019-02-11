package training.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class Test1 {
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

        String browserStr = System.getProperty("browser");
        log.debug("browser property: '" + browserStr + "'");
        if (browserStr == null || browserStr.isEmpty()) {
            log.warn("Unknown browser property, use 'c' for Chrome, 'f' for Firefox, 'i' for Internet Explorer. (Using Chrome by default now)");
            browser = "c";
        }
        else
            browser = browserStr;

        if (browser.equalsIgnoreCase("c")) {
            log.info("Chrome browser start");
            driver = new ChromeDriver();
        }
        else if (browser.equalsIgnoreCase("f")) {
            log.info("Firefox browser start");
            driver = new FirefoxDriver();
        }
        else if (browser.equalsIgnoreCase("i")) {
            log.info("Internet Explorer browser start");
            driver = new InternetExplorerDriver();
        }
        else {
            log.error("Unknown browser '" + browser + "', use 'c' for Chrome, 'f' for Firefox, 'i' for Internet Explorer. (System.exit now)");
            System.exit(1);
        }

        wait = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        log.debug("start function finished");
    }

    @Test
    public void test2() {
        log.debug("test2 started");

        driver.navigate().to("http://localhost/litecart/admin");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
        wait.until(ExpectedConditions.titleIs("My Store"));

        log.debug("test2 finidhed");
    }

    @Test
    public void test1() {
        log.debug("test1 started");

        driver.navigate().to("http://www.google.com");
        driver.findElement(By.name("q")).sendKeys("webdriver");
        WebElement btn = driver.findElement(By.name("btnK"));
        wait.until(ExpectedConditions.visibilityOf(btn));
        btn.click();
        wait.until(ExpectedConditions.titleIs("webdriver - Поиск в Google"));

        log.debug("test1 finidhed");
    }

    @After
    public void stop() throws InterruptedException {
        log.debug("stop function started");

        Thread.sleep(sleepTime);
        if (browser.equalsIgnoreCase("i")) {
            driver.close();
            log.debug("driver closed");
        }
        driver.quit();
        log.debug("driver quitted");
        driver = null;

        log.debug("stop function finished");
    }
}
