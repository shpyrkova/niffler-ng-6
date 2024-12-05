package guru.qa.niffler.condition;

import com.codeborne.selenide.*;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class SpendsConditions {

    public static WebElementsCondition spends(SpendJson... expectedSpendings) {
        return new WebElementsCondition() {

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedSpendings)) {
                    throw new IllegalArgumentException("No expected spendings parameters given");
                }
                if (expectedSpendings.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedSpendings.length, elements.size());
                    return rejected(message, elements);
                }

                final List<String> actualSpendingsList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final SpendJson expectedSpending = Arrays.stream(expectedSpendings).toList().get(i);
                    final String category = expectedSpending.category().name();
                    final String amount = convertAmountToText(expectedSpending.amount(), expectedSpending.currency());
                    final String description = expectedSpending.description();
                    final String date = convertDateToText(expectedSpending.spendDate());

                    final ElementsCollection spendingCells = $$(elements).find(text(description)).$$("td");
                    final String actualCategory = spendingCells.get(1).getText();
                    final String actualAmount = spendingCells.get(2).getText();
                    final String actualDescription = spendingCells.get(3).getText();
                    final String actualDate = spendingCells.get(4).getText();

                    actualSpendingsList.add(getSpendingsTextFromActualData(actualCategory, actualAmount, actualDescription, actualDate));

                    if (!actualCategory.equals(category)) {
                        String message = String.format(
                                "Spend category mismatch (expected: %s, actual: %s)",
                                category, actualCategory
                        );
                        return rejected(message, actualSpendingsList);
                    }
                    if (!actualAmount.equals(amount)) {
                        String message = String.format(
                                "Spend amount mismatch (expected: %s, actual: %s)",
                                amount, actualAmount
                        );
                        return rejected(message, actualSpendingsList);
                    }
                    if (!actualDescription.equals(description)) {
                        String message = String.format(
                                "Spend description mismatch (expected: %s, actual: %s)",
                                description, spendingCells.get(3).getText()
                        );
                        return rejected(message, actualSpendingsList);
                    }
                    if (!actualDate.equals(date)) {
                        String message = String.format(
                                "Spend date mismatch (expected: %s, actual: %s)",
                                date, actualDate
                        );
                        return rejected(message, actualSpendingsList);
                    }
                }
                return accepted();
            }

            @Override
            public String toString() {
                return getSpendingsTextFromSpendJson(expectedSpendings).toString();
            }
        };
    }

    private static String convertDateToText(Date spendingDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return formatter.format(spendingDate);
    }

    private static String convertAmountToText(Double amount, CurrencyValues curr) {
        return amount.intValue() + " " + curr.symbol;
    }

    private static String getSpendingsTextFromActualData(String category, String amount, String description, String date) {
        return String.format("{Category: %s, Amount: %s, Description: %s, Date: %s}", category, amount, description, date);
    }

    private static List<String> getSpendingsTextFromSpendJson(SpendJson... spendings) {
        List<String> spendingsText = new ArrayList<>();
        for (SpendJson spend : spendings) {
            String category = spend.category().name();
            String amount = convertAmountToText(spend.amount(), spend.currency());
            String description = spend.description();
            String date = convertDateToText(spend.spendDate());

            String spendText = String.format("{Category: %s, Amount: %s, Description: %s, Date: %s}", category, amount, description, date);
            spendingsText.add(spendText);
        }
        return spendingsText;
    }

}