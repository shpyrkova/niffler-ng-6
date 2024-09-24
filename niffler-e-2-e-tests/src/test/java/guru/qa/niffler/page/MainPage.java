package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final SelenideElement historyOfSpendingsHeader = $(byText("History of Spendings"));
    private final SelenideElement profileMenuButton = $x("//*[@id=\"root\"]/header/div/div[2]/button");
    private final SelenideElement profileLink = $("[href='/profile']");
    private final SelenideElement friendsLink = $("[href='/people/friends']");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
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
        tableRows.find(text(spendingDescription)).should(visible);
    }

    public void statisticsHeaderShouldBePresent() {
        statisticsHeader.shouldBe(visible);
    }

    public void historyOfSpendingsHeaderShouldBePresent() {
        historyOfSpendingsHeader.shouldBe(visible);
    }

}
