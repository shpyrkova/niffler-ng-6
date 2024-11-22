package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendsConditions;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class SpendingTable extends BaseComponent<SpendingTable> {

    public SpendingTable() {
        super($("#spendings"));
    }

    private final ElementsCollection spendingTableRows = self.$("tbody").$$("tr");
    private final SelenideElement historyOfSpendingsHeader = self.$(byText("History of Spendings"));
    private final SelenideElement periodDropdown = self.$("#period");
    private final SelenideElement deleteButton = self.$("#delete");
    private final SelenideElement nextButton = self.$("#page-next");
    private final SelenideElement dialog = $("div[role='dialog']");

    protected final SearchField searchField = new SearchField();

    @Nonnull
    private SearchField getSearchField() {
        return searchField;
    }

    @Step("Выбрать период {period}")
    public void selectPeriod(DataFilterValues period) {
        periodDropdown.click();
        $(byText(period.text)).click();
    }

    @Nonnull
    @Step("Перейти к редактированию траты {description}")
    public EditSpendingPage toEditSpendingPage(String description) {
        spendingTableRows.find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Удалить трату {description}")
    public void deleteSpending(String description) {
        spendingTableRows.find(text(description)).$$("td").get(0).click();
        deleteButton.click();
        dialog.$(byText("Delete")).click();
    }

    private SelenideElement searchSpendingByDescription(String description) {
        spendingTableRows.shouldBe(sizeGreaterThan(0));
        searchField.search(description);
        while (!spendingTableRows.findBy(text(description)).isDisplayed()) {
            if (nextButton.isEnabled()) {
                nextButton.click();
                spendingTableRows.shouldBe(sizeGreaterThan(0));
            } else {
                throw new AssertionError("Spending not found");
            }
        }
        return spendingTableRows.findBy(text(description));
    }

    @Step("Проверить, что таблица содержит траты {expectedSpends}")
    public void checkTableContains(String... expectedSpends) {
        for (String description : expectedSpends) {
            SelenideElement spend = spendingTableRows.findBy(text(description));
            spend.shouldBe(visible);
        }
    }

    @Step("Проверить, что таблица содержит все {expectedSpends}")
    public void checkTable(SpendJson... expectedSpends) {
        spendingTableRows.should(SpendsConditions.spends(expectedSpends));
    }

    @Step("Проверить, что в таблице {expectedSize} строк")
    public void checkTableSize(int expectedSize) {
        spendingTableRows.shouldHave(size(expectedSize));
    }

    @Step("Проверить, что присутствует заголовок History of spendings")
    public void checkThatHistoryOfSpendingsHeaderIsPresent() {
        historyOfSpendingsHeader.shouldBe(visible);
    }

}
