package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    private final SelenideElement friendsTab = $("#simple-tabpanel-friends");
    private final SelenideElement noFriendsMessage = friendsTab.$(byText("There are no users yet"));
    private final ElementsCollection friendsTableRows = $("#friends").$$("tr");
    private final ElementsCollection requestsTableRows = $("#requests").$$("tr");
    private final SelenideElement allPeopleTab = $("[href='/people/all']");
    private final SelenideElement nextButton = friendsTab.$("#page-next");
    private final SelenideElement dialog = $("div[role='dialog']");

    protected final SearchField searchField = new SearchField();

    @Nonnull
    private SearchField getSearchField() {
        return searchField;
    }
    @Step("Проверить, что присутствует строка друга {username}")
    public void checkThatFriendRowIsPresent(String username) {
        searchFriendByUsername(username).shouldBe(visible);
    }

    @Step("Проверить, что присутствует строка с входящим приглашением {username}")
    public void checkThatIncomeRequestIsPresent(String username) {
        requestsTableRows.find(text(username)).shouldBe(visible);
    }

    @Step("Проверить, что отсутствует строка с входящим приглашением {username}")
    public void checkThatIncomeRequestIsAbsent(String username) {
        requestsTableRows.find(text(username)).shouldNotBe(visible);
    }

    @Step("Проверить, что присутствует сообщение There are no users yet")
    public void checkThatNoFriendsMessageIsPresent() {
        noFriendsMessage.shouldBe(visible);
    }

    @Step("Перейти на вкладку All people")
    public void clickAllPeopleTab() {
        allPeopleTab.click();
    }

    @Step("Найти друга {username}")
    public SelenideElement searchFriendByUsername(String username) {
        friendsTableRows.shouldBe(sizeGreaterThan(0)); // Ждем подгрузки строк таблицы
        searchField.search(username);
        return friendsTableRows.findBy(text(username)); // Возвращаем строку с найденным другом
    }

    @Step("Найти входящий запрос от {username}")
    public SelenideElement searchRequestByUsername(String username) {
        requestsTableRows.shouldBe(sizeGreaterThan(0)); // Ждем подгрузки строк таблицы
        searchField.search(username);
        return requestsTableRows.findBy(text(username)); // Возвращаем строку с найденным другом
    }

    @Step("Принять входящий запрос от {username}")
    public void acceptIncomeInvitation(String username) {
        searchRequestByUsername(username).$(byText("Accept")).click();
    }

    @Step("Отклонить входящий запрос от {username}")
    public void declineIncomeInvitation(String username) {
        searchRequestByUsername(username).$(byText("Decline")).click();
        dialog.$(byText("Decline")).click();
    }

}
