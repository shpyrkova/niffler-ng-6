package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.*;

public class SpendingWebTest extends TestBaseWeb {

    @User(
            spendings = @Spending(
                    category = "cat_8",
                    description = "Обучение Advanced 2.0",
                    amount = 79990)
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTableTest(UserJson user) {
        String actualDescription = user.testData().spendings().getFirst().description();
        final String newDescription = "Обучение Niffler Next Generation";

        loginPage.login(user.username(), user.testData().password())
                .getSpendingTable().toEditSpendingPage(actualDescription)
                .setNewSpendingDescription(newDescription)
                .save();
        mainPage.checkThatSpendingUpdateMessageIsPresent();
        mainPage.getSpendingTable().checkTableContains(newDescription);
    }

    @User
    @Test
    void createSpendingTest(UserJson user) {
        String amount = String.valueOf(randomAmount());
        CurrencyValues currency = randomCurrency();
        String category = randomCategoryName();
        Date date = randomSpendingDate();
        String description = randomSpendingDescription();

        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toAddSpendingPage();
        newSpendingPage.setAmount(amount);
        newSpendingPage.setCurrency(currency);
        newSpendingPage.setCategory(category);
        newSpendingPage.setSpendingDate(date);
        newSpendingPage.setDescription(description);
        newSpendingPage.clickAddButton();
        mainPage.getSpendingTable().checkTableContains(description);
    }

}

