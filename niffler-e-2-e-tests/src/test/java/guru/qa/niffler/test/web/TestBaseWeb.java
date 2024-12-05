package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.*;
import org.junit.jupiter.api.extension.ExtendWith;

@WebTest
@ExtendWith(BrowserExtension.class)
public class TestBaseWeb {

    protected final RegisterPage registerPage = new RegisterPage();
    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();
    protected final NewSpendingPage newSpendingPage = new NewSpendingPage();
    protected final ProfilePage profilePage = new ProfilePage();
    protected final FriendsPage friendsPage = new FriendsPage();
    protected final PeoplePage peoplePage = new PeoplePage();

}
