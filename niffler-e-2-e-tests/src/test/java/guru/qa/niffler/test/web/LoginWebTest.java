package guru.qa.niffler.test.web;

import org.junit.jupiter.api.Test;

public class LoginWebTest extends TestBaseWeb {

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
