package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
    private final SelenideElement noFriendsMessage = $("#simple-tabpanel-friends").$(byText("There are no users yet"));
    private final ElementsCollection friendsTableRows = $("#friends").$$("tr");
    private final ElementsCollection requestsTableRows = $("#requests").$$("tr");
    private final SelenideElement allPeopleTab = $("[href='/people/all']");
    private final ElementsCollection allPeopleTableRows = $("#all").$$("tr");

    private SelenideElement friendUsername(String username) {
        return friendsTableRows.find(text(username)).$(byText(username));
    }

    private SelenideElement incomeRequestUsername(String username) {
        return requestsTableRows.find(text(username)).$(byText(username));
    }

    private SelenideElement outcomeRequestRow(String username) {
        return allPeopleTableRows.find(text(username));
    }

    public void friendUsernameShouldBePresent(String username) {
        friendUsername(username).shouldBe(visible);
    }

    public void requestUsernameShouldBePresent(String username) {
        incomeRequestUsername(username).shouldBe(visible);
    }

    public void outcomeRequestShouldBePresent(String username) {
        outcomeRequestRow(username).shouldBe(visible).shouldHave(text("Waiting..."));
    }

    public void noFriendsMessageShouldBePresent() {
        noFriendsMessage.shouldBe(visible);
    }

    public void clickAllPeopleTab() {
        allPeopleTab.click();
    }

}
