package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @Before
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");

        if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
            // Firefox-specific options
            options.addPreference("dom.webdriver.enabled", false);
            options.addPreference("useAutomationExtension", false);
            options.setCapability("moz:firefoxOptions", options);
            options.setCapability("acceptInsecureCerts", true);
            driver = new FirefoxDriver(options);
        } else {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }

        // Увеличиваем таймауты для Firefox
        int timeout = browser.equalsIgnoreCase("firefox") ? 20 : 15;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        driver.get("https://qa-scooter.praktikum-services.ru/");
        closeCookieBanner();
    }

    private void closeCookieBanner() {
        try {
            driver.findElement(By.id("rcc-confirm-button")).click();
        } catch (Exception e) {
            System.out.println("Cookie banner not found or could not be closed: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}