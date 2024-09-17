package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegisterWebTest {

    RegisterPage registerPage = new RegisterPage();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();

    @Test
    void shouldRegisterNewUser() {
        final String username = Faker.instance().internet().domainName();
        final String password = "00000000";

        loginPage.clickCreateNewAccountLink()
                .register(username, password);
        registerPage.clickSignInLink();
        loginPage.login(username, password);
        mainPage.statisticsHeaderShouldBePresent();
        mainPage.historyOfSpendingsHeaderShouldBePresent();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        final String username = "dasha";
        final String password = "00000000";

        loginPage.clickCreateNewAccountLink()
                .register(username, password);
        registerPage.userWithUsernameAlreadyExistsErrorMessageShouldBePresent(username);
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
        registerPage.passwordsShouldBeEqualErrorMessageShouldBePresent();
    }

}
