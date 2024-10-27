package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

    private final SelenideElement friendsTab = $("#simple-tabpanel-friends");
    private final SelenideElement noFriendsMessage = friendsTab.$(byText("There are no users yet"));
    private final ElementsCollection friendsTableRows = $("#friends").$$("tr");
    private final ElementsCollection requestsTableRows = $("#requests").$$("tr");
    private final SelenideElement allPeopleTab = $("[href='/people/all']");
    private final ElementsCollection allPeopleTableRows = $("#all").$$("tr");
    private final SelenideElement nextButton = friendsTab.$("#page-next");

    public void checkThatFriendRowIsPresent(String username) {
        findFriendByName(username).shouldBe(visible);
    }

    public void checkThatIncomeRequestIsPresent(String username) {
        requestsTableRows.find(text(username)).shouldBe(visible);
    }

    public void checkThatOutcomeRequestIsPresent(String username) {
        allPeopleTableRows.find(text(username)).shouldBe(visible).shouldHave(text("Waiting..."));
    }

    public void checkThatNoFriendsMessageIsPresent() {
        noFriendsMessage.shouldBe(visible);
    }

    public void clickAllPeopleTab() {
        allPeopleTab.click();
    }

    public SelenideElement findFriendByName(String username) {
        friendsTableRows.shouldBe(sizeGreaterThan(0)); // Ждем подгрузки строк таблицы
        while (!friendsTableRows.findBy(text(username)).isDisplayed()) {
            if (nextButton.isEnabled()) {
                nextButton.click(); // Нажимаем кнопку Next, если она доступна
                friendsTableRows.shouldBe(sizeGreaterThan(0));
            } else {
                throw new AssertionError("Friend " + username + " not found"); // Завершаем тест, если друга нет на последней странице
            }
        }
        return friendsTableRows.findBy(text(username)); // Возвращаем строку с найденным другом
    }


}
