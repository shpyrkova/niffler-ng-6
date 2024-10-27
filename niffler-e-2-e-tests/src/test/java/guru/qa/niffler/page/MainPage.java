package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {
    private final SelenideElement spendingsArea = $("#spendings");
    private final ElementsCollection spendingTableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final SelenideElement historyOfSpendingsHeader = $(byText("History of Spendings"));
    private final SelenideElement profileMenuButton = $x("//*[@id=\"root\"]/header/div/div[2]/button");
    private final SelenideElement profileLink = $("[href='/profile']");
    private final SelenideElement friendsLink = $("[href='/people/friends']");
    private final SelenideElement nextButton = spendingsArea.$("#page-next");

    public EditSpendingPage editSpending(String spendingDescription) {
        spendingTableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public void clickProfileMenuButton() {
        profileMenuButton.click();
    }

    public void clickProfileLink() {
        profileLink.click();
    }

    public void clickFriendsLink() {
        friendsLink.click();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        findSpendingByDescription(spendingDescription).shouldBe(visible);
    }

    public void statisticsHeaderShouldBePresent() {
        statisticsHeader.shouldBe(visible);
    }

    public void historyOfSpendingsHeaderShouldBePresent() {
        historyOfSpendingsHeader.shouldBe(visible);
    }

    public SelenideElement findSpendingByDescription(String description) {
        spendingTableRows.shouldBe(sizeGreaterThan(0));
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

}
