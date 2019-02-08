package training.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.concurrent.TimeUnit;

public class Test1 {
    private boolean isChrome = true;

    public WebDriver driver;
    public WebDriverWait wait;
    private String title;

    @Before
    public void start() {
        // isChrome = false;
        
        title = "webdriver - Поиск в Google";
        if (isChrome)
            driver = new ChromeDriver();
        else
            driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void test1() {
        driver.navigate().to("http://www.google.com");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.name("q")).sendKeys("webdriver");
        WebElement btn = driver.findElement(By.name("btnK"));
        wait.until(ExpectedConditions.visibilityOf(btn));
        btn.click();
        wait.until(ExpectedConditions.titleIs(title));
    }

    @After
    public void stop() {
        driver.quit();
        driver = null;
    }
}
