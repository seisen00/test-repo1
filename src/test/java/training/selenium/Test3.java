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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOError;

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

    public String getNormalizedPath(String path) {
        try {
            return Paths.get(path).toAbsolutePath().normalize().toString();
        } catch (IOError e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                String message = e.getMessage();
                if (message == null)
                    log.error(e.toString());
                else
                    log.error(message);
            }
            else
                log.error(cause.getMessage());
            return null;
        }
    }

    @Test
    public void test2() throws InterruptedException {
        log.debug("test2 started");

        String picturePath = getNormalizedPath("./src/test/resources/duck.jpeg");
        log.info("picture path: " + picturePath);

        try {
            driver.navigate().to("http://localhost/litecart/admin");
            driver.findElement(By.name("username")).sendKeys("admin");
            driver.findElement(By.name("password")).sendKeys("admin");
            driver.findElement(By.name("login")).click();
            wait.until(titleIs("My Store"));

            driver.navigate().to("http://localhost/litecart/admin/?app=catalog&doc=catalog");
            wait.until(titleIs("Catalog | My Store"));
            log.info("add new product");
            String xpathNewProduct = "//*[@id='content']//a[contains(@href, 'edit_product')]";
            driver.findElement(By.xpath(xpathNewProduct)).click();
            wait.until(titleIs("Add New Product | My Store"));

            // fill General
            String xpathFile = "//*[@id='tab-general']//input[@type='file']";
            driver.findElement(By.xpath(xpathFile)).sendKeys(picturePath);
            String xpathStatus = "//*[@id='tab-general']//input[@type='radio' and @value='1']";
            driver.findElement(By.xpath(xpathStatus)).sendKeys(Keys.SPACE, Keys.TAB, "duck1", Keys.TAB, "1", Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.SPACE, Keys.TAB, Keys.TAB, "1", Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, "03052019", Keys.TAB, "03052020");

            // fill Information
            String xpathInformation = "//*[@id='content']//a[@href='#tab-information']";
            driver.findElement(By.xpath(xpathInformation)).click();
            String xpathManufacturer = "//*[@id='tab-information']//select[@name='manufacturer_id']";
            WebElement weManufacturer = wait.until(presenceOfElementLocated(By.xpath(xpathManufacturer)));
            wait.until(elementToBeClickable(weManufacturer));
            String newProductName = "duck1";
            weManufacturer.sendKeys(Keys.DOWN, Keys.TAB, Keys.TAB, newProductName, Keys.TAB, "short duck", Keys.TAB, "long duck", Keys.TAB, "duck title", Keys.TAB, "duck meta");

            // fill Prices
            String xpathPrices = "//*[@id='content']//a[@href='#tab-prices']";
            driver.findElement(By.xpath(xpathPrices)).click();
            String xpathPurchasePrice = "//*[@id='tab-prices']//input[@name='purchase_price']";
            WebElement wePurchasePrice = wait.until(presenceOfElementLocated(By.xpath(xpathPurchasePrice)));
            wait.until(elementToBeClickable(wePurchasePrice));
            wePurchasePrice.sendKeys(Keys.HOME, Keys.chord(Keys.CONTROL, "a"), "2.2", Keys.TAB, Keys.DOWN, Keys.TAB, Keys.TAB, Keys.TAB, "2", Keys.TAB, "0.2", Keys.TAB, "0.5", Keys.TAB, "0.01");

            // save and check
            String xpathSaveButton = "//*[@id='content']//button[@name='save']";
            driver.findElement(By.xpath(xpathSaveButton)).click();
            wait.until(titleIs("Catalog | My Store"));
            String xpathNewProductName = "//*[@id='content']//a[contains(.,'" + newProductName + "')]";
            WebElement weNewProductName = driver.findElement(By.xpath(xpathNewProductName));
            log.info("new product name found: " + newProductName);
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test1 finished");
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
