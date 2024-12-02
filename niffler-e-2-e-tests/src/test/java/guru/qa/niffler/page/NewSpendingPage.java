package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class NewSpendingPage extends BasePage<NewSpendingPage> {

    public static final String URL = CFG.frontUrl() + "spending";

    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement currencyDropdown = $("#currency");
    private final ElementsCollection currencyList = $$("ul[role=listbox]");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement addButton = $("#save");

    protected  final Calendar calendar = new Calendar();

    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Nonnull
    public Calendar getCalendar() {
        return calendar;
    }

    @Step("Заполнить сумму")
    public void setAmount(String amount) {
        amountInput.setValue(amount);
    }

    @Step("Выбрать валюту")
    public void setCurrency(CurrencyValues currency) {
        currencyDropdown.click();
        currencyList.findBy(text(currency.name())).click();
    }

    @Step("Заполнить категорию")
    public void setCategory(String category) {
        categoryInput.setValue(category);
    }

    @Step("Заполнить дату")
    public void setSpendingDate(Date date) {
        getCalendar().selectDateInCalendar(date);
    }

    @Step("Заполнить описание")
    public void setDescription(String description) {
        descriptionInput.setValue(description);
    }

    @Step("Нажать Добавить трату")
    public void clickAddButton() {
        addButton.click();
    }

}
