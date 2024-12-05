package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "main";

    private final SelenideElement spendingsArea = $("#spendings");
    private final ElementsCollection spendingTableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final SelenideElement dialog = $("div[role='dialog']");

    protected final SpendingTable spendingTable = new SpendingTable();
    protected final StatComponent statComponent = new StatComponent();

    public void open() {
        Selenide.open(URL);
    }

    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Nonnull
    public SpendingTable getSpendingTable() {
        return spendingTable;
    }

    @Nonnull
    public StatComponent getStatComponent() {
        statComponent.getSelf().scrollIntoView(true);
        return statComponent;
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
