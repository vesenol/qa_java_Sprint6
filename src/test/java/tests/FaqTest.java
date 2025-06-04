package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.BaseTest;
import utils.TestData;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FaqTest extends BaseTest {
    private final int questionIndex;
    private final String expectedAnswer;

    public FaqTest(int questionIndex, String expectedAnswer) {
        this.questionIndex = questionIndex;
        this.expectedAnswer = expectedAnswer;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return TestData.getFaqData();
    }

    @Test
    public void testFaqAnswers() {
        By questionLocator = By.id("accordion__heading-" + questionIndex);
        By answerLocator = By.id("accordion__panel-" + questionIndex);

        WebElement question = wait.until(ExpectedConditions.elementToBeClickable(questionLocator));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", question);

        // Добавляем небольшую паузу после скролла
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        question.click();

        WebElement answer = wait.until(ExpectedConditions.visibilityOfElementLocated(answerLocator));
        String actualAnswer = answer.getText();
        assertEquals("Неверный ответ на вопрос", expectedAnswer, actualAnswer);
    }
}