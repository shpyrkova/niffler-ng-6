package guru.qa.niffler.test.web;

import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
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

    @ScreenShotTest(value = "img/expected-edit-spend-test.png")
    @User(
            spendings = {@Spending(
                    category = "edit spending test",
                    description = "Продукты",
                    amount = 11500),
                    @Spending(
                            category = "second",
                            description = "Cinema",
                            amount = 11500)}
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
        mainPage.getStatComponent().checkThatStatPieChartAsExpected(expected);
        String category = user.testData().spendings().getFirst().category().name();
        CurrencyValues currency = user.testData().spendings().getFirst().currency();
        mainPage.getStatComponent().checkSpendsLegendLabel(category, newAmount, currency);
        String expectedText = category + " " + newAmountStr + " " + CurrencyValues.RUB.symbol;
        String category2 = user.testData().spendings().get(1).category().name();
        String expectedText2 = category2 + " " + "11500" + " " + CurrencyValues.RUB.symbol;
        mainPage.getStatComponent().checkBubbles(new Bubble(Color.yellow, expectedText), new Bubble(Color.green, expectedText2));
        mainPage.getStatComponent().checkBubblesContains(new Bubble(Color.green, expectedText2));
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
        mainPage.getStatComponent().checkThatStatPieChartAsExpected(expected);
        Double amount = user.testData().spendings().getFirst().amount();
        CurrencyValues currency = user.testData().spendings().getFirst().currency();
        mainPage.getStatComponent().checkArchivedSpendsLegendLabel(amount, currency);
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
        mainPage.getStatComponent().checkThatStatPieChartAsExpected(expected);
    }

    @Test
    @User(
            spendings = {@Spending(
                    category = "first",
                    description = "Hobby",
                    amount = 20500),
                    @Spending(
                            category = "second",
                            description = "Products",
                            amount = 11500)
            }
    )
    void fullSpendingsTableTest(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        SpendJson expectedSpend = user.testData().spendings().get(0);
        SpendJson expectedSpend2 = user.testData().spendings().get(1);
        mainPage.getSpendingTable().checkTable(expectedSpend, expectedSpend2);
    }


}

