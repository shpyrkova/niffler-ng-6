package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($("#root header"));
    }

    private final SelenideElement mainPageLink = self.$("a[href*='/main']");
    private final SelenideElement addSpendingButton = self.$("a[href*='/spending']");
    private final SelenideElement menuButton = self.$("button");
    private final SelenideElement menu = $("ul[role='menu']");
    private final SelenideElement friendsLink = menu.$("a[href='/people/friends']");
    private final SelenideElement allPeopleLink = menu.$("a[href='/people/all']");
    private final SelenideElement profileLink = menu.$("a[href='/profile']");
    private final SelenideElement signOutLink = menu.$(byText("Sign out"));

    @Step("Перейти на страницу Friends")
    public void toFriendsPage() {
        menuButton.click();
        friendsLink.click();
    }

    @Step("Перейти на страницу All people")
    public void toAllPeoplesPage() {
        menuButton.click();
        allPeopleLink.click();
    }

    @Step("Перейти на страницу Profile")
    public void toProfilePage() {
        menuButton.click();
        profileLink.click();
    }

    @Step("Выбрать в меню Log out")
    public void signOut() {
        menuButton.click();
        signOutLink.click();
    }

    @Step("Перейти на страницу New spending")
    public void toAddSpendingPage() {
        addSpendingButton.click();
    }

    @Step("Перейти на Главную по нажатию на логотип")
    public void toMainPage() {
        mainPageLink.click();
    }

}