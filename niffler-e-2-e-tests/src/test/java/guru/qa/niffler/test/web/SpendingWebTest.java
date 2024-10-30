package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class SpendingWebTest extends TestBaseWeb {

    @User(
            spendings = @Spending(
                    category = "cat_8",
                    description = "Обучение Advanced 2.0",
                    amount = 79990)
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
        String actualDescription = user.testData().spendings().getFirst().description();
        final String newDescription = "Обучение Niffler Next Generation";

                loginPage.login(user.username(), user.testData().password())
                .editSpending(actualDescription)
                .setNewSpendingDescription(newDescription)
                .save();

        mainPage.checkThatTableContainsSpending(newDescription);
    }

}

