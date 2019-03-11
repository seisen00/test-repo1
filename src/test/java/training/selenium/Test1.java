package training.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Test1 {
    public WebDriver driver;
    public WebDriverWait wait;

    private static final Logger log = LogManager.getLogger(Test1.class);
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

    static ExpectedCondition<String> thereIsWindowOtherThan(final Set<String> oldWindows) {
        return new ExpectedCondition<String>() {
            @Override
            public String apply(WebDriver driver) {
                Set<String> currentWindows = driver.getWindowHandles();
                if (currentWindows.size() != oldWindows.size()) {
                    currentWindows.removeAll(oldWindows);
                    if (currentWindows.size() != 1)
                        return null;
                    return currentWindows.iterator().next();
                }
                return null;
            }

            @Override
            public String toString() {
                return "find new window handle wich is not present in set: " + oldWindows;
            }
        };
    }

    @Test
    public void test7() throws InterruptedException {
        log.debug("test7 started");

        try {
            driver.navigate().to("http://localhost/litecart/admin");
            driver.findElement(By.name("username")).sendKeys("admin");
            driver.findElement(By.name("password")).sendKeys("admin");
            driver.findElement(By.name("login")).click();
            wait.until(titleIs("My Store"));

            // edit country
            driver.navigate().to("http://localhost/litecart/admin/?app=countries&doc=countries");
            String countriesTitle = "Countries | My Store";
            wait.until(titleIs(countriesTitle));
            log.info("edit country");
            String xpathCountries = "//form[@name='countries_form']//a[@title='Edit']";
            driver.findElements(By.xpath(xpathCountries)).get(0);
            wait.until(elementToBeClickable(driver.findElements(By.xpath(xpathCountries)).get(0))).click();
            wait.until(titleIs("Edit Country | My Store"));

            String mainWindow = driver.getWindowHandle();
            log.debug("main window handle: " + mainWindow);
            Set<String> oldWindows;
            String newWindow = "";

            // open links in new Windows
            String xpathLinks = "//*[@id='content']//a[@target='_blank' and not(@title='Help')]";
            ArrayList<WebElement> links = new ArrayList<WebElement>(driver.findElements(By.xpath(xpathLinks)));
            log.info("links number found: " + links.size());
            WebElement link;
            for (Object linkObj: links) {
                oldWindows = driver.getWindowHandles();
                link = (WebElement) linkObj;
                log.info("click link: " + link.getAttribute("pathname"));
                wait.until(elementToBeClickable(link)).click();
                newWindow = wait.until(thereIsWindowOtherThan(oldWindows));
                log.debug("new window handle: " + newWindow);
                driver.switchTo().window(newWindow);
                Thread.sleep(1000);
                driver.close();
                driver.switchTo().window(mainWindow);
            }
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test7 finished");
    }

    class Product {
        public String name;
        public String priceRegular;
        public String priceCampain;
        public String priceRegularColor;
        public String priceCampainColor;
        public String fontRegularStyle;
        public String fontCampainStyle;
        public Dimension priceRegularSize;
        public Dimension priceCampainSize;

        public boolean checkColors() {
            int colorR = -1;
            int colorG = -1;
            int colorB = -1;
            int colorA = -1;

            Pattern p = Pattern.compile("rgb(a)?\\( *([0-9]+), *([0-9]+), *([0-9]+)(, *([0-9]+))?\\)");
            Matcher m = null;

            m = p.matcher(priceRegularColor);
            if (!m.matches()) {
                throw new WebDriverException("incorrect regular color format: " + priceRegularColor);
            } else {
                colorR = Integer.parseInt(m.group(2));
                colorG = Integer.parseInt(m.group(3));
                colorB = Integer.parseInt(m.group(4));
                if (m.groupCount() == 6 && m.group(6) != null)
                    colorA = Integer.parseInt(m.group(6));
                log.debug(" regular colorR: " + colorR + ", colorG: " + colorG + ", colorB: " + colorB + ", colorA: " + colorA);
                if (!((colorR == colorG) && (colorG == colorB)))
                    throw new WebDriverException("incorrect regular color: " + colorR + " " + colorG + " " + colorB);
            }

            m = p.matcher(priceCampainColor);
            if (!m.matches()) {
                throw new WebDriverException("incorrect campain color format: " + priceCampainColor);
            } else {
                colorR = Integer.parseInt(m.group(2));
                colorG = Integer.parseInt(m.group(3));
                colorB = Integer.parseInt(m.group(4));
                if (m.groupCount() == 6 && m.group(6) != null)
                    colorA = Integer.parseInt(m.group(6));
                log.debug(" campain colorR: " + colorR + ", colorG: " + colorG + ", colorB: " + colorB + ", colorA: " + colorA);
                if (!((colorG == 0) && (colorB == 0)))
                    throw new WebDriverException("incorrect campain color: " + colorR + " " + colorG + " " + colorB);
            }

            return true;
        }

        public boolean checkSizes() {
            return priceRegularSize.getWidth() < priceCampainSize.getWidth() && priceRegularSize.getHeight() < priceCampainSize.getHeight();
        }

        public boolean checkFontStyles() {
            if (!browser.equalsIgnoreCase("i")) { // there is no 'text-decoration-line' in IE
                if (!fontRegularStyle.equals("line-through"))
                    throw new WebDriverException("incorrect main product regular font style: '" + fontRegularStyle + "'");
            }
            if (!(fontCampainStyle.equals("700") || fontCampainStyle.equals("900"))) // bold: 700 - in Chrome, 900 - in Firefox
                throw new WebDriverException("incorrect main product campain font style: '" + fontCampainStyle + "'");
            return true;
        }

        public boolean compareTo(Product other) {
            return toString().compareTo(other.toString()) == 0;
        }

        public String toString() {
            return name + "_" + priceRegular + "_" + priceCampain;
        }
        public String printFullInfo() {
            return toString() + "_" + priceRegularColor + "_" + priceCampainColor + "_" + fontRegularStyle + "_" + fontCampainStyle + "_" + priceRegularSize + "_" + priceCampainSize;
        }
    }

    @Test
    public void test6() {
        log.debug("test6 started");

        try {
            driver.navigate().to("http://localhost/litecart");
            wait.until(titleIs("Online Store | My Store"));

            Product productFromMain = new Product();
            Product productFromPage = new Product();
            String xpathOnMainProduct = "//div[@id='box-campaigns']//a[@class='link']";
            String xpathOnPageProduct = "//div[@id='box-product']//img[@class='image']";
            String xpathPriceMainRegular = ".//s[@class='regular-price']";
            String xpathPricePageRegular = "//div[@class='information']//s[@class='regular-price']";
            String xpathPriceMainCampain = ".//strong[@class='campaign-price']";
            String xpathPricePageCampain = "//div[@class='information']//strong[@class='campaign-price']";

            WebElement link = driver.findElement(By.xpath(xpathOnMainProduct));
            productFromMain.name = link.getAttribute("title");
            log.info("product from main name: '" + productFromMain.name + "'");
            WebElement priceMainRegular = link.findElement(By.xpath(xpathPriceMainRegular));
            productFromMain.priceRegular = priceMainRegular.getText();
            log.info("product from main price regular: '" + productFromMain.priceRegular + "'");
            productFromMain.priceRegularColor = priceMainRegular.getCssValue("color");
            log.info("product from main price regular color: '" + productFromMain.priceRegularColor + "'");
            productFromMain.priceRegularSize = priceMainRegular.getSize();
            log.info("product from main price regular size: '" + productFromMain.priceRegularSize + "'");
            productFromMain.fontRegularStyle = priceMainRegular.getCssValue("text-decoration-line");
            log.info("product from main regular font style: " + productFromMain.fontRegularStyle);
            WebElement priceMainCampain = link.findElement(By.xpath(xpathPriceMainCampain));
            productFromMain.priceCampain = priceMainCampain.getText();
            log.info("product from main price campain: '" + productFromMain.priceCampain + "'");
            productFromMain.priceCampainColor = priceMainCampain.getCssValue("color");
            log.info("product from main price campain color: '" + productFromMain.priceCampainColor + "'");
            productFromMain.priceCampainSize = priceMainCampain.getSize();
            log.info("product from main price campain size: '" + productFromMain.priceCampainSize + "'");
            productFromMain.fontCampainStyle = priceMainCampain.getCssValue("font-weight");
            log.info("product from main campain font style: " + productFromMain.fontCampainStyle);

            link.click();
            wait.until(titleContains("Subcategory"));
            productFromPage.name = driver.findElement(By.xpath(xpathOnPageProduct)).getAttribute("title");
            log.info("product from page name: '" + productFromPage.name + "'");
            WebElement pricePageRegular = driver.findElement(By.xpath(xpathPricePageRegular));
            productFromPage.priceRegular = pricePageRegular.getText();
            log.info("product from page price regular: '" + productFromPage.priceRegular + "'");
            productFromPage.priceRegularColor = pricePageRegular.getCssValue("color");
            log.info("product from page price regular color: '" + productFromPage.priceRegularColor + "'");
            productFromPage.priceRegularSize = pricePageRegular.getSize();
            log.info("product from page price regular size: '" + productFromPage.priceRegularSize + "'");
            productFromPage.fontRegularStyle = pricePageRegular.getCssValue("text-decoration-line");
            log.info("product from page regular font style: " + productFromPage.fontRegularStyle);
            WebElement pricePageCampain = driver.findElement(By.xpath(xpathPricePageCampain));
            productFromPage.priceCampain = pricePageCampain.getText();
            log.info("product from page price campain: '" + productFromPage.priceCampain + "'");
            productFromPage.priceCampainColor = pricePageCampain.getCssValue("color");
            log.info("product from page price campain color: '" + productFromPage.priceCampainColor + "'");
            productFromPage.priceCampainSize = pricePageCampain.getSize();
            log.info("product from page price campain size: '" + productFromPage.priceCampainSize + "'");
            productFromPage.fontCampainStyle = pricePageCampain.getCssValue("font-weight");
            log.info("product from page campain font style: " + productFromPage.fontCampainStyle);

            // а, б
            if (!productFromMain.compareTo(productFromPage))
                throw new WebDriverException("products are different:\n from main = '" + productFromMain.toString() + "'\n from page = '" + productFromPage.toString() + "'");

            // в, г
            productFromMain.checkColors();
            productFromMain.checkFontStyles();
            productFromPage.checkColors();
            productFromPage.checkFontStyles();

            // д
            if (!productFromMain.checkSizes())
                throw new WebDriverException("incorrect main product size values: '" + productFromMain.printFullInfo() + "'");
            if (!productFromPage.checkSizes())
                throw new WebDriverException("incorrect page product size values: '" + productFromPage.printFullInfo() + "'");
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test6 finished");
    }

    private void checkSorting(String xpathLocator) {
        String entryOrig = "";
        String entrySorted = "";
        ArrayList<WebElement> enntriesOrig = new ArrayList<WebElement>(driver.findElements(By.xpath(xpathLocator)));
        ArrayList<WebElement> entriesSorted = new ArrayList<WebElement>(enntriesOrig);
        log.info("entries size: " + entriesSorted.size());
        entriesSorted.sort(Comparator.comparing(WebElement::getText));
        log.info("entries sorted size: " + entriesSorted.size());
        for (int i = 0; i < enntriesOrig.size(); i++) {
            entryOrig = enntriesOrig.get(i).getText();
            entrySorted = entriesSorted.get(i).getText();
            log.info("next entry(" + (i + 1) + "): " + entryOrig);
            if (!entryOrig.equals(entrySorted))
                throw new WebDriverException("entries[" + (i + 1) + "] are not sorted; orig='" + entryOrig + "', sorted='" + entrySorted + "'");
        }
    }

    @Test
    public void test5() {
        log.debug("test5 started");

        try {
            driver.navigate().to("http://localhost/litecart/admin");
            driver.findElement(By.name("username")).sendKeys("admin");
            driver.findElement(By.name("password")).sendKeys("admin");
            driver.findElement(By.name("login")).click();
            wait.until(titleIs("My Store"));

            List<WebElement> rows;
            WebElement row;
            int rowsNum = 0;
            int rowsIndex = 0;

            // 1 а)
            driver.navigate().to("http://localhost/litecart/admin/?app=countries&doc=countries");
            String countriesTitle = "Countries | My Store";
            wait.until(titleIs(countriesTitle));
            log.info("check countries sorting");
            String xpathCountries = "//form[@name='countries_form']//a[not(@title='Edit')]";
            checkSorting(xpathCountries);

            // 1 б)
            String xpathCountriesRows = "//form[@name='countries_form']//tr[@class='row']";
            String xpathZone = "./td[6]";
            String xpathZoneEdit = "./td[7]/a";
            String xpathZones = "//table[@id='table-zones']//input[contains(@name, 'name') and not(@value='')]";
            WebElement zone;
            WebElement zoneEdit;
            int zonesCount = 0;

            rows = driver.findElements(By.xpath(xpathCountriesRows));
            rowsNum = rows.size();
            rowsIndex = 0;
            do {
                rows = driver.findElements(By.xpath(xpathCountriesRows));
                row = rows.get(rowsIndex);
                zone = row.findElement(By.xpath(xpathZone));
                log.info("next zone(" + (rowsIndex + 1) + "): " + zone.getText());
                zonesCount = Integer.parseInt(zone.getText());
                if (zonesCount > 0) {
                    log.info(zonesCount + " zones found");
                    zoneEdit = row.findElement(By.xpath(xpathZoneEdit));
                    zoneEdit.click();
                    wait.until(titleIs("Edit Country | My Store"));
                    log.info("check zones sorting");
                    checkSorting(xpathZones);
                    driver.navigate().back();
                    wait.until(titleIs(countriesTitle));
                }
            } while (++rowsIndex < rowsNum);

            // 2
            driver.navigate().to("http://localhost/litecart/admin/?app=geo_zones&doc=geo_zones");
            String geoZonesTitle = "Geo Zones | My Store";
            wait.until(titleIs(geoZonesTitle));

            String xpathGeoZonesRows = "//form[@name='geo_zones_form']//tr[@class='row']";
            String xpathGeoZoneEdit = "./td[5]/a";
            String xpathGeoZones = "//table[@id='table-zones']//select[contains(@name, 'zone_code')]/option[@selected='selected']";
            WebElement geoZoneEdit;

            rows = driver.findElements(By.xpath(xpathGeoZonesRows));
            rowsNum = rows.size();
            rowsIndex = 0;
            do {
                rows = driver.findElements(By.xpath(xpathGeoZonesRows));
                row = rows.get(rowsIndex);
                geoZoneEdit = row.findElement(By.xpath(xpathGeoZoneEdit));
                geoZoneEdit.click();
                wait.until(titleIs("Edit Geo Zone | My Store"));
                log.info("check geo zones sorting");
                checkSorting(xpathGeoZones);
                driver.navigate().back();
                wait.until(titleIs(geoZonesTitle));
            } while (++rowsIndex < rowsNum);
        } catch (WebDriverException e) {
            log.error(e.getMessage());
            throw e;
        }

        log.debug("test5 finished");
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
            wait.until(titleIs("Online Store | My Store"));

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

        log.debug("test4 finished");
    }

    @Test
    public void test3() {
        log.debug("test3 started");

        driver.navigate().to("http://localhost/litecart/admin");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
        wait.until(titleIs("My Store"));

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
                    wait.until(visibilityOf(link));
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
                        wait.until(visibilityOf(link));
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

        log.debug("test3 finished");
    }

    @Test
    public void test2() {
        log.debug("test2 started");

        driver.navigate().to("http://localhost/litecart/admin");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
        wait.until(titleIs("My Store"));

        log.debug("test2 finished");
    }

    @Test
    public void test1() {
        log.debug("test1 started");

        driver.navigate().to("http://www.google.com");
        driver.findElement(By.name("q")).sendKeys("webdriver");
        WebElement btn = driver.findElement(By.name("btnK"));
        wait.until(elementToBeClickable(btn));
        btn.click();
        wait.until(titleIs("webdriver - Поиск в Google"));

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
