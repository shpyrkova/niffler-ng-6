package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginWebTest {

    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();

    @Test
    void mainPageShouldBeDisplayedAfterSuccessfulLogin() {
        final String username = "zoomer";
        final String password = "00000000";

        loginPage.login(username, password);
        mainPage.statisticsHeaderShouldBePresent();
        mainPage.historyOfSpendingsHeaderShouldBePresent();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        final String username = "zoomer";
        final String password = "99999999";

        loginPage.login(username, password);
        loginPage.invalidCredentialsErrorMessageShouldBePresent();
    }

}
