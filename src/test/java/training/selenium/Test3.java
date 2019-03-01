package training.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import org.openqa.selenium.support.ui.Select;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Random;

public class Test3 {
    public WebDriver driver;
    public WebDriverWait wait;

    private static final Logger log = LogManager.getLogger(Test3.class);
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

        log.debug("start function finished");
    }

    public class Account {
        public String nameFirst = "bob";
        public String nameLast = "robins";
        public String address = "ul. some";
        public String postcode = "12345";
        public String city = "Coldberg";
        public String country = "US";
        public String state = "AK";
        public String email;
        public String phone = "+1-322-223-3-22";
        public String password = "qwerty";

        public Account() {
            email = getRandomString() + "@company.com";
        }

        private String getRandomString() {
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)(random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            return buffer.toString();
        }
    }

    @Test
    public void test1() throws InterruptedException {
        log.debug("test1 started");

        String xpathNewCustomerLink = "//aside[@id='navigation']//a[contains(@href, 'create_account')]";
        String xpathCountry = "//select[@name='country_code']";
        String xpathState = "//select[@name='zone_code']";
        String xpathNameFirst = "//input[@name='firstname']";
        String xpathLogout = "//div[@id='box-account']/div/ul/li[4]/a";
        String xpathLogin = "//button[@name='login']";
        String xpathEmail = "//input[@name='email']";
        Account acc1 = new Account();
        log.info("account: email='" + acc1.email + "', password='" + acc1.password + "'");

        try {
            driver.navigate().to("http://localhost/litecart");
            wait.until(titleIs("Online Store | My Store"));

            // create and logout
            driver.findElement(By.xpath(xpathNewCustomerLink)).click();
            wait.until(titleIs("Create Account | My Store"));
            WebElement weCountry = driver.findElement(By.xpath(xpathCountry));
            wait.until(elementToBeClickable(weCountry));
            Select selectCountry = new Select(weCountry);
            selectCountry.selectByValue(acc1.country);
            WebElement weState = driver.findElement(By.xpath(xpathState));
            wait.until(elementToBeClickable(weState));
            Select selectState = new Select(weState);
            selectState.selectByValue(acc1.state);

            WebElement weNameFirst = driver.findElement(By.xpath(xpathNameFirst));
            weNameFirst.sendKeys(acc1.nameFirst, Keys.TAB, acc1.nameLast, Keys.TAB, acc1.address, Keys.TAB, Keys.TAB, acc1.postcode, Keys.TAB, acc1.city, Keys.TAB, Keys.TAB, Keys.TAB, acc1.email, Keys.TAB, acc1.phone, Keys.TAB, Keys.SPACE, Keys.TAB, acc1.password, Keys.TAB, acc1.password, Keys.TAB, Keys.ENTER);
            Thread.sleep(sleepTime);
            wait.until(presenceOfElementLocated(By.xpath(xpathLogout)));
            WebElement weLogout = driver.findElement(By.xpath(xpathLogout));
            wait.until(elementToBeClickable(weLogout));
            weLogout.click();
            Thread.sleep(sleepTime);

            // login and logout
            wait.until(presenceOfElementLocated(By.xpath(xpathLogin)));
            WebElement weLogin = driver.findElement(By.xpath(xpathLogin));
            wait.until(elementToBeClickable(weLogin));
            WebElement weEmail = driver.findElement(By.xpath(xpathEmail));
            weEmail.sendKeys(acc1.email, Keys.TAB, acc1.password, Keys.TAB, Keys.TAB, Keys.ENTER);
            Thread.sleep(sleepTime);
            wait.until(presenceOfElementLocated(By.xpath(xpathLogout)));
            weLogout = driver.findElement(By.xpath(xpathLogout));
            wait.until(elementToBeClickable(weLogout));
            weLogout.click();
            Thread.sleep(sleepTime);
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test1 finished");
    }

    @After
    public void stop() throws InterruptedException {
        log.debug("stop function started");

        Thread.sleep(sleepTime);
        if (!browser.equalsIgnoreCase("f")) {
            driver.close();
            log.debug("driver closed");
        }
        driver.quit();
        log.debug("driver quitted");
        driver = null;

        log.debug("stop function finished");
    }
}
