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
import java.util.List;

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

        log.debug("start function finished");
    }

    @Test
    public void test4() {
        log.debug("test4 started");

        List<WebElement> products;
        List<WebElement> stickers;
        WebElement product;
        String cssProducts = "li[class ^= product]";
        String cssSticker = "div[class = image-wrapper] div[class ^= sticker]";
        // String xpathProducts = "//li[starts-with(@class, 'product')]/a[@class='link' and contains(@title, ' Duck') and ./div[@class='name' and contains(., ' Duck')]]";
        // String xpathSticker = "./div[@class='image-wrapper']/div[starts-with(@class, 'sticker ')]";

        try {
            driver.navigate().to("http://localhost/litecart");
            wait.until(ExpectedConditions.titleIs("Online Store | My Store"));

            log.debug("find all Duck products by '" + cssProducts + "'");
            products = driver.findElements(By.cssSelector(cssProducts));
            // products = driver.findElements(By.xpath(xpathProducts));
            log.info("productsNum: " + products.size());
            for (Object productObj: products) {
                product = (WebElement) productObj;
                log.info("next product: " + product.getText());
                log.debug("\tfind elements by '" + cssSticker + "'");
                stickers = product.findElements(By.cssSelector(cssSticker));
                // stickers = product.findElements(By.xpath(xpathSticker));
                log.info("stickersNum: " + stickers.size());
                if (stickers.size() != 1)
                    throw new WebDriverException("product '" + product.getText() + "' have stickers number not equals one: " + stickers.size());
                log.info("\tsticker: " + stickers.get(0).getText());
            }
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test4 finidhed");
    }

    @Test
    public void test3() {
        log.debug("test3 started");

        driver.navigate().to("http://localhost/litecart/admin");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
        wait.until(ExpectedConditions.titleIs("My Store"));

        List<WebElement> menuItems;
        List<WebElement> submenuItems;
        WebElement menuItem;
        WebElement submenuItem;
        WebElement link;
        WebElement header;
        String cssItems = "#box-apps-menu > #app-";
        String cssSubitems = "#box-apps-menu > #app- > ul.docs > li";
        String cssHeader = "#content > h1";
        int itemsNum;
        int itemsIndex;
        int subitemsNum;
        int subitemsIndex;

        try {
            menuItems = driver.findElements(By.cssSelector(cssItems));
            if (menuItems.isEmpty()) {
                log.error("no items in menu");
            } else {
                itemsNum = menuItems.size();
                log.info("itemsNum: " + itemsNum);

                itemsIndex = 0;
                do {
                    log.debug("find elements by '" + cssItems + "'");
                    menuItems = driver.findElements(By.cssSelector(cssItems));
                    if (itemsIndex >= menuItems.size())
                        throw new WebDriverException("something totally changed. old menu index: " + itemsIndex + "; new menu items size: " + menuItems.size());
                    log.debug("obtain menu item number " + (itemsIndex + 1));
                    menuItem = menuItems.get(itemsIndex);
                    log.info("menuItem[" + (itemsIndex + 1) + "]: " + menuItem.getText());
                    link = menuItem.findElement(By.tagName("a"));
                    log.info("link: " + link.getText());
                    wait.until(ExpectedConditions.visibilityOf(link));
                    link.click();
                    log.debug("link clicked");
                    header = driver.findElement(By.cssSelector(cssHeader));
                    log.info("header: " + header.getText());

                    submenuItems = driver.findElements(By.cssSelector(cssSubitems));
                    if (submenuItems.isEmpty()) {
                        log.debug("no subitems in menu item");
                        continue;
                    }
                    subitemsNum = submenuItems.size();
                    log.info("subitemsNum: " + subitemsNum);

                    subitemsIndex = 0;
                    do {
                        log.debug("\tfind elements by '" + cssSubitems + "'");
                        submenuItems = driver.findElements(By.cssSelector(cssSubitems));
                        if (subitemsIndex >= submenuItems.size())
                            throw new WebDriverException("something totally changed. old submenu index: " + subitemsIndex + "; new submenu items size: " + submenuItems.size());
                        log.debug("\tobtain submenu item number " + (subitemsIndex + 1));
                        submenuItem = submenuItems.get(subitemsIndex);
                        log.info("\tsubmenuItem[" + (subitemsIndex + 1) + "]: " + submenuItem.getText());
                        link = submenuItem.findElement(By.tagName("a"));
                        log.info("\tlink: " + link.getText());
                        wait.until(ExpectedConditions.visibilityOf(link));
                        link.click();
                        log.debug("\tlink clicked");
                        header = driver.findElement(By.cssSelector(cssHeader));
                        log.info("\theader: " + header.getText());
                    } while (++subitemsIndex < subitemsNum);
                } while (++itemsIndex < itemsNum);
            }
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test3 finidhed");
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
