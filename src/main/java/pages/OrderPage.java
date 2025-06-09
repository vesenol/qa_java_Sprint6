package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class OrderPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы формы заказа
    private final By firstNameField = By.xpath(".//input[@placeholder='* Имя']");
    private final By lastNameField = By.xpath(".//input[@placeholder='* Фамилия']");
    private final By addressField = By.xpath(".//input[@placeholder='* Адрес: куда привезти заказ']");
    private final By metroStationField = By.xpath(".//input[@placeholder='* Станция метро']");
    private final By phoneField = By.xpath(".//input[@placeholder='* Телефон: на него позвонит курьер']");
    private final By nextButton = By.xpath(".//button[text()='Далее']");

    // Локаторы формы аренды
    private final By dateField = By.xpath(".//input[@placeholder='* Когда привезти самокат']");
    private final By rentalPeriodField = By.className("Dropdown-placeholder");
    private final By colorBlackCheckbox = By.id("black");
    private final By colorGreyCheckbox = By.id("grey");
    private final By commentField = By.xpath(".//input[@placeholder='Комментарий для курьера']");
    private final By orderButton = By.xpath("//*[@id='root']/div/div[2]/div[3]/button[2]");
    private final By confirmOrderButton = By.xpath("//button[contains(text(), 'Да')]");
    private final By orderSuccessModal = By.xpath("//div[contains(@class, 'Order_Modal')]");
    private final By orderModal = By.xpath("//div[contains(@class, 'Order_Modal')]");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void fillFirstStep(String firstName, String lastName, String address, String metroStation, String phone) {
        System.out.println("[DEBUG] Заполнение первой страницы заказа");
        waitAndSendKeys(firstNameField, firstName);
        waitAndSendKeys(lastNameField, lastName);
        waitAndSendKeys(addressField, address);

        WebElement metroField = wait.until(ExpectedConditions.elementToBeClickable(metroStationField));
        scrollToElement(metroField);
        metroField.click();

        WebElement station = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(String.format(".//div[text()='%s']", metroStation))));
        station.click();

        waitAndSendKeys(phoneField, phone);

        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        scrollToElement(nextBtn);
        nextBtn.click();
        System.out.println("[DEBUG] Первая страница заполнена успешно");
    }

    public void fillSecondStep(String date, String rentalPeriod, String color, String comment) {
        System.out.println("[DEBUG] Заполнение второй страницы заказа");

        // Ввод даты
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(dateField));
        dateInput.clear();
        dateInput.sendKeys(date);
        dateInput.sendKeys(Keys.ENTER);

        // Выбор срока аренды
        WebElement rentalElement = wait.until(ExpectedConditions.elementToBeClickable(rentalPeriodField));
        scrollToElement(rentalElement);
        rentalElement.click();

        WebElement periodOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(String.format(".//div[text()='%s']", rentalPeriod))));
        scrollToElement(periodOption);
        periodOption.click();

        // Выбор цвета
        if ("black".equalsIgnoreCase(color)) {
            clickWithScroll(colorBlackCheckbox);
        } else if ("grey".equalsIgnoreCase(color)) {
            clickWithScroll(colorGreyCheckbox);
        }

        // Ввод комментария
        if (comment != null && !comment.isEmpty()) {
            waitAndSendKeys(commentField, comment);
        }

        // Нажатие кнопки Заказать
        clickOrderButton();
    }

    public boolean waitForOrderModal() {
        try {
            System.out.println("[DEBUG] Ожидание модального окна подтверждения заказа");
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(orderModal));
            takeScreenshot("order_modal_appeared");
            System.out.println("[DEBUG] Модальное окно подтверждения заказа появилось");
            return true;
        } catch (TimeoutException e) {
            System.err.println("[ERROR] Модальное окно подтверждения не появилось в течение 30 секунд");
            takeScreenshot("order_modal_missing");
            return false;
        }
    }

    private void clickOrderButton() {
        try {
            System.out.println("[DEBUG] Попытка нажать кнопку 'Заказать'");

            // Ожидание и проверка кнопки
            WebElement orderBtn = wait.until(driver -> {
                WebElement btn = driver.findElement(orderButton);
                if (btn.isDisplayed() && btn.isEnabled()) {
                    System.out.println("[DEBUG] Кнопка 'Заказать' найдена и доступна");
                    return btn;
                }
                System.out.println("[DEBUG] Кнопка 'Заказать' не доступна для клика");
                return null;
            });

            // Скролл и дополнительная проверка
            scrollToElement(orderBtn);
            wait.until(ExpectedConditions.elementToBeClickable(orderButton));

            // Клик через JavaScript
            System.out.println("[DEBUG] Выполняем клик по кнопке 'Заказать'");
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", orderBtn);

            System.out.println("[DEBUG] Кнопка 'Заказать' успешно нажата");
        } catch (Exception e) {
            System.err.println("[ERROR] Ошибка при нажатии кнопки 'Заказать': " + e.getMessage());
            takeScreenshot("order_button_error");
            throw new RuntimeException("Не удалось нажать кнопку 'Заказать'", e);
        }
    }

    // Добавляем новый метод для явной проверки успешного оформления
    public void verifyOrderCompletion() {
        try {
            // 1. Проверяем, что модальное окно подтверждения появилось
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessModal));

            // 2. Проверяем, что есть текст "Заказ оформлен"
            WebElement successText = modal.findElement(By.xpath(".//div[contains(text(), 'Заказ оформлен')]"));

            // 3. Делаем скриншот успешного состояния
            takeScreenshot("order_success_confirmation");

            System.out.println("[SUCCESS] Заказ успешно оформлен");
        } catch (TimeoutException e) {
            System.err.println("[ERROR] Не появилось подтверждение оформления заказа");
            takeScreenshot("order_confirmation_missing");
            throw new AssertionError("Подтверждение заказа не появилось после нажатия кнопки 'Да'");
        }
    }

    // Модифицируем метод confirmOrder()
    public void confirmOrder() {
        try {
            System.out.println("[DEBUG] Подтверждение заказа");

            // Ждем и кликаем кнопку "Да"
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmOrderButton));
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", confirmBtn);

            // Добавляем явную проверку завершения заказа
            verifyOrderCompletion();

        } catch (Exception e) {
            System.err.println("[ERROR] Ошибка подтверждения заказа: " + e.getMessage());
            takeScreenshot("confirm_order_error");
            throw new RuntimeException("Не удалось подтвердить заказ", e);
        }
    }

    public boolean isOrderSuccessModalDisplayed() {
        try {
            boolean isDisplayed = wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessModal))
                    .isDisplayed();
            System.out.println("[DEBUG] Модальное окно успешного заказа " + (isDisplayed ? "отображается" : "не отображается"));
            return isDisplayed;
        } catch (Exception e) {
            System.out.println("[INFO] Модальное окно успешного заказа не отображается: " + e.getMessage());
            return false;
        }
    }

    private void waitAndSendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
        System.out.println("[DEBUG] Введен текст '" + text + "' в поле " + locator);
    }

    private void clickWithScroll(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollToElement(element);
        element.click();
        System.out.println("[DEBUG] Выполнен клик по элементу " + locator);
    }

    private void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor)driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});",
                    element);
            Thread.sleep(200);
            System.out.println("[DEBUG] Проскроллено к элементу");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void takeScreenshot(String name) {
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