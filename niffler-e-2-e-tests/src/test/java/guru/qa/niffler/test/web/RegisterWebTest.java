package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.closeWebDriver;

@ExtendWith(BrowserExtension.class)
public class RegisterWebTest {

    private static final Config CFG = Config.getInstance();
    RegisterPage registerPage = new RegisterPage();
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
    void shouldRegisterNewUser() {
        final String username = Faker.instance().internet().domainName();
        final String password = "00000000";

        loginPage.clickCreateNewAccountLink()
                .register(username, password);
        registerPage.clickSignInLink();
        loginPage.login(username, password);
        mainPage.statisticsHeader.shouldBe(visible);
        mainPage.historyOfSpendingsHeader.shouldBe(visible);
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        final String username = "dasha";
        final String password = "00000000";

        loginPage.clickCreateNewAccountLink()
                .register(username, password);
        registerPage.userWithUsernameAlreadyExistsErrorMessage(username).shouldBe(visible);
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        final String username = Faker.instance().internet().domainName();
        final String password = "00000000";
        final String confirmPassword = "11111111";

        loginPage.clickCreateNewAccountLink();
        registerPage.setUsername(username);
        registerPage.setPassword(password);
        registerPage.confirmPassword(confirmPassword);
        registerPage.clickSignUpButton();
        registerPage.passwordsShouldBeEqualErrorMessage().shouldBe(visible);
    }

}
