package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class LoginWebTest extends TestBaseWeb {

    @User(
            categories = {
                    @Category(name = "cat_1", archived = false),
                    @Category(name = "cat_2", archived = true),
            },
            spendings = {
                    @Spending(
                            category = "cat_3",
                            description = "test_spend",
                            amount = 100
                    )
            }
    )
    @Test
    void mainPageShouldBeDisplayedAfterSuccessfulLogin(UserJson user) {
        final String username = user.username();
        final String password = user.testData().password();
        System.out.println("!!!!!!!!!!" + username);

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
