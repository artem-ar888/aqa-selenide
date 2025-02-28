package ru.netology.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

class CardDeliveryTest {

    private String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeEach
    void setUp() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    void shouldSuccessfulFormSubmission() {
        String planningDate = generateDate(4, "dd.MM.yyyy");

        $("[data-test-id='city'] input").setValue("Киров");
        $("[data-test-id='date'] input.input__control")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иван Иванов-Петров");
        $("[data-test-id='phone'] input").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.text("Забронировать")).click();
        $("[data-test-id='notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    void shouldSuccessfullySubmitFormWithDropdownCityAndDatePicker() {
        String cityName = "Калуга";
        String planningDate = generateDate(7, "dd.MM.yyyy");

        // Находим поле ввода и вводим первые две буквы города
        $("[data-test-id='city'] input").setValue(cityName.substring(0, 2));

        // проверяем, что список городов отобразился
        $(".popup_visible").shouldBe(Condition.visible);

        // Выбираем нужный город из списка
        $$(".popup_visible")
                .findBy(Condition.text(cityName))
                .click();

        // Проверяем, что в поле ввода теперь стоит "Калуга"
        $("[data-test-id='city'] input").shouldHave(Condition.value(cityName));

        // Нажимаем на кнопку отображения виджета календаря
        $("[data-test-id='date'] button").click();

        // Перемещаемся по числам вперед на 4 дня (по умолчанию дата уже сдвинута на 3 дня)
        $(".calendar")
                .shouldBe(Condition.visible)
                .sendKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ENTER);

        // Проверяем, что в поле ввода теперь стоит дата на неделю вперёд от текущей даты
        $("[data-test-id='date'] input.input__control").shouldHave(Condition.value(planningDate));

        $("[data-test-id='name'] input").setValue("Иван Иванов-Петров");
        $("[data-test-id='phone'] input").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.text("Забронировать")).click();
        $("[data-test-id='notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }
}