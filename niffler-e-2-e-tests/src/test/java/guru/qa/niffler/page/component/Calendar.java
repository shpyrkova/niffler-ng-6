package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Calendar extends BaseComponent<Calendar> {

    private final SelenideElement calendarButton = $("button[aria-label*='Choose date']");
    private final SelenideElement previousMonthButton = self.$("button[title='Previous month']");
    private final SelenideElement nextMonthButton = self.$("button[title='Next month']");
    private final SelenideElement currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
    private final ElementsCollection dateRows = self.$$(".MuiDayCalendar-weekContainer");

    public Calendar() {
        super($(".MuiPickersLayout-root"));
    }

    @Step("Выбрать дату в календаре: {date}")
    public void selectDateInCalendar(Date date) {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        calendarButton.click();
        selectYear(String.valueOf(cal.get(YEAR)));
        final int desiredMonthIndex = cal.get(MONTH);
        selectMonth(desiredMonthIndex);
        selectDay(cal.get(DAY_OF_MONTH));
    }

    private void selectYear(String expectedYear) {
        clickCurrentMonthAndYear();
        self.$$(".MuiPickersYear-yearButton").findBy(text(expectedYear)).scrollTo().click();
    }

    private void clickCurrentMonthAndYear() {
        currentMonthAndYear.click();
    }

    private void selectMonth(int expectedMonthIndex) {
        int actualMonth = getActualMonthIndex();

        while (actualMonth > expectedMonthIndex) {
            previousMonthButton.click();
            Selenide.sleep(200);
            actualMonth = getActualMonthIndex();
        }
        while (actualMonth < expectedMonthIndex) {
            nextMonthButton.click();
            Selenide.sleep(200);
            actualMonth = getActualMonthIndex();
        }

    }

    private int getActualMonthIndex() {
        return Month.valueOf(currentMonthAndYear.getText()
                        .split(" ")[0]
                        .toUpperCase())
                .ordinal();
    }

    private void selectDay(int desiredDay) {
        ElementsCollection rows = dateRows.snapshot();

        for (SelenideElement row : rows) {
            ElementsCollection days = row.$$("button").snapshot();
            for (SelenideElement day : days) {
                if (day.getText().equals(String.valueOf(desiredDay))) {
                    day.click();
                    return;
                }
            }
        }
    }

}
