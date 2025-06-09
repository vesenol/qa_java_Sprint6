package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pages.MainPage;
import pages.OrderPage;
import utils.BaseTest;
import utils.TestData;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class OrderTest extends BaseTest {
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final String date;
    private final String rentalPeriod;
    private final String color;
    private final String comment;
    private boolean useTopButton;

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return TestData.getOrderData();
    }

    public OrderTest(String firstName, String lastName, String address, String metroStation,
                     String phone, String date, String rentalPeriod, String color, String comment) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.date = date;
        this.rentalPeriod = rentalPeriod;
        this.color = color;
        this.comment = comment;
        this.useTopButton = true;
    }

    @Test
    public void testOrderScooter() {
        MainPage mainPage = new MainPage(driver);
        OrderPage orderPage = new OrderPage(driver);

        if (useTopButton) {
            mainPage.clickOrderButtonTop();
        } else {
            mainPage.clickOrderButtonBottom();
        }

        orderPage.fillFirstStep(firstName, lastName, address, metroStation, phone);
        orderPage.fillSecondStep(date, rentalPeriod, color, comment);

        // Добавляем проверку перед подтверждением
        assertTrue("Модальное окно подтверждения не появилось",
                orderPage.waitForOrderModal());

        orderPage.confirmOrder();

        // Явная проверка завершения заказа
        try {
            orderPage.verifyOrderCompletion();
        } catch (AssertionError e) {
            // Делаем дополнительный скриншот
            orderPage.takeScreenshot("final_order_verification_failed");
            throw e;
        }
    }

    @Test
    public void testOrderScooterWithBottomButton() {
        useTopButton = false;
        testOrderScooter();
    }
}