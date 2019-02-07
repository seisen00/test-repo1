package training.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class Test1 {
    private boolean isChrome = true;

    public WebDriver driver;
    public WebDriverWait wait;
    private String title;

    @Before
    public void start() {
        // isChrome = false;
        // System.setProperty("webdriver.chrome.driver", "/data/bin/webdriver/chromedriver");
        // System.setProperty("webdriver.gecko.driver", "/data/bin/webdriver/geckodriver");
        
        title = "webdriver - Поиск в Google";
        if (isChrome) {
            driver = new ChromeDriver();
        }
        else {
            driver = new FirefoxDriver();
            title += " - Mozilla Firefox";
        }
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void test1() {
        driver.navigate().to("http://www.google.com");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.name("q")).sendKeys("webdriver");
        driver.findElement(By.name("btnK")).click();
        wait.until(titleIs(title));
    }

    @After
    public void stop() {
        driver.quit();
        driver = null;
    }

}
