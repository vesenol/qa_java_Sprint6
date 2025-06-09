package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class MainPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы кнопок заказа
    private final By orderButtonTop = By.xpath("//button[contains(@class, 'Button_Button__ra12g') and text()='Заказать']");
    private final By orderButtonBottom = By.xpath("(//button[contains(@class, 'Button_Button__ra12g') and text()='Заказать'])[2]");

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickOrderButtonTop() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(orderButtonTop));
        takeScreenshot("before_top_order_button_click");
        btn.click();
        takeScreenshot("after_top_order_button_click");
    }

    public void clickOrderButtonBottom() {
        // Прокручиваем страницу вниз перед поиском кнопки
        ((org.openqa.selenium.JavascriptExecutor)driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(orderButtonBottom));
        takeScreenshot("before_bottom_order_button_click");
        btn.click();
        takeScreenshot("after_bottom_order_button_click");
    }

    private void takeScreenshot(String name) {
        try {
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            String timestamp = String.valueOf(System.currentTimeMillis());
            FileUtils.copyFile(screenshot,
                    new File("target/screenshots/" + name + "_" + timestamp + ".png"));
            System.out.println("[DEBUG] Скриншот сохранен: " + name);
        } catch (Exception e) {
            System.err.println("[ERROR] Не удалось сохранить скриншот: " + e.getMessage());
        }
    }
}