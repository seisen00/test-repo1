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

    @Before
    public void start() {
        // isChrome = false;
        
        if (isChrome)
            driver = new ChromeDriver();
        else
            driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void test2() {
        driver.navigate().to("http://localhost/litecart/admin");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
        wait.until(ExpectedConditions.titleIs("My Store"));
    }

    @Test
    public void test1() {
        driver.navigate().to("http://www.google.com");
        driver.findElement(By.name("q")).sendKeys("webdriver");
        WebElement btn = driver.findElement(By.name("btnK"));
        wait.until(ExpectedConditions.visibilityOf(btn));
        btn.click();
        wait.until(ExpectedConditions.titleIs("webdriver - Поиск в Google"));
    }

    @After
    public void stop() {
        driver.quit();
        driver = null;
    }
}
