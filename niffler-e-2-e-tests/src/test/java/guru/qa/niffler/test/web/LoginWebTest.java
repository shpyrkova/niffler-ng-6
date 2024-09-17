package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@ExtendWith(BrowserExtension.class)
public class LoginWebTest {

    private static final Config CFG = Config.getInstance();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();

    @BeforeEach
    void beforeEach() {
        Selenide.open(CFG.frontUrl());
    }

    @AfterEach
    void afterEach() {
        closeWebDriver();
    }

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
