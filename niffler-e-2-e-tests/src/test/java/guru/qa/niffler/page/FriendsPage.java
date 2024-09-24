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

    public void friendRowShouldBePresent(String username) {
        friendsTableRows.find(text(username)).shouldBe(visible);
    }

    public void incomeRequestShouldBePresent(String username) {
        requestsTableRows.find(text(username)).shouldBe(visible);
    }

    public void outcomeRequestShouldBePresent(String username) {
        allPeopleTableRows.find(text(username)).shouldBe(visible).shouldHave(text("Waiting..."));
    }

    public void noFriendsMessageShouldBePresent() {
        noFriendsMessage.shouldBe(visible);
    }

    public void clickAllPeopleTab() {
        allPeopleTab.click();
    }

}
