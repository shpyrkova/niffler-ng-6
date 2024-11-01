package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    private final SelenideElement spendingsArea = $("#spendings");
    private final ElementsCollection spendingTableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final SelenideElement dialog = $("div[role='dialog']");

    protected final SpendingTable spendingTable = new SpendingTable();

    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Nonnull
    public SpendingTable getSpendingTable() {
        return spendingTable;
    }

    @Step("Проверить, что присутствует заголовок Statistics")
    public void statisticsHeaderShouldBePresent() {
        statisticsHeader.shouldBe(visible);
    }

    @Step("Подтвердить выход из системы")
    public void clickDialogLogOutButton() {
        dialog.$(byText("Log out")).click();
    }

    @Step("Проверить, что показано уведомление об успешном редактировании траты")
    public void checkThatSpendingUpdateMessageIsPresent() {
        checkAlert("Spending is edited successfully");
    }

}
