package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

public class RegisterWebTest extends TestBaseWeb {

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
