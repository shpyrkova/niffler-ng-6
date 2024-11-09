package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.*;

public class SpendingWebTest extends TestBaseWeb {

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

    @ScreenShotTest("img/expected-edit-spend-test.png")
    @User(
            spendings = @Spending(
                    category = "edit spending test",
                    description = "Продукты",
                    amount = 11500)
    )
    @Test
    void editSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        String actualDescription = user.testData().spendings().getFirst().description();
        final String newDescription = randomSpendingDescription();
        // хардкод, потому что сравниваем с эталонным скриншотом
        final Double newAmount = 12300.0;
        final String newAmountStr = String.valueOf(newAmount.intValue());

        loginPage.login(user.username(), user.testData().password())
                .getSpendingTable().toEditSpendingPage(actualDescription)
                .setNewSpendingAmount(newAmountStr)
                .setNewSpendingDescription(newDescription)
                .save();
        mainPage.checkThatSpendingUpdateMessageIsPresent();
        mainPage.getSpendingTable().checkTableContains(newAmountStr);
        mainPage.checkThatStatPieChartAsExpected(expected);
        String category = user.testData().spendings().getFirst().category().name();
        CurrencyValues currency = user.testData().spendings().getFirst().currency();
        mainPage.checkSpendsLegendLabel(category, newAmount, currency);
    }

    @ScreenShotTest("img/expected-archive-stat-test.png")
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    void checkArchiveStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toProfilePage();
        profilePage.archiveCategory(user.testData().spendings().getFirst().category().name());
        profilePage.getHeader().toMainPage();
        mainPage.checkThatStatPieChartAsExpected(expected);
        Double amount = user.testData().spendings().getFirst().amount();
        CurrencyValues currency = user.testData().spendings().getFirst().currency();
        mainPage.checkArchivedSpendsLegendLabel(amount, currency);
    }

    @ScreenShotTest("img/expected-delete-spend-test.png")
    @User(
            spendings = @Spending(
                    category = "Отпуск",
                    description = "Отпуск со своей в Турции",
                    amount = 350000
            )
    )
    void deleteSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        loginPage.login(user.username(), user.testData().password());
        mainPage.getSpendingTable().deleteSpending(user.testData().spendings().getFirst().description());
        mainPage.checkThatStatPieChartAsExpected(expected);
    }

}

