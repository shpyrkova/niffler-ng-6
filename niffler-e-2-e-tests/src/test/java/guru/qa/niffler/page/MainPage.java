package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    private final SelenideElement spendingsArea = $("#spendings");
    private final ElementsCollection spendingTableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final SelenideElement dialog = $("div[role='dialog']");
    private final SelenideElement statPieChart = $("canvas[role='img']");
    private final ElementsCollection legendLabels = $("#legend-container").$$("li");


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

    @Step("Проверить, что пайчарт статистики соответствует ожидаемому")
    public void checkThatStatPieChartAsExpected(BufferedImage expected) throws IOException {
        sleep(2000); // сложно что-то иначе дождаться загрузки анимации... может, Дима расскажет в лекции про custom conditions?))
        BufferedImage actual = ImageIO.read($(statPieChart).screenshot());
        assertThat(new ScreenDiffResult(actual, expected).getAsBoolean()).isFalse();
    }

    @Step("Проверить, что лейбл с тратами по категории {categoryName} содержит верный текст")
    public void checkSpendsLegendLabel(String categoryName, Double amount, CurrencyValues cur) {
        String amountString = String.valueOf(amount.intValue());
        String currency = cur.symbol;
        legendLabels.findBy(text(categoryName + " " + amountString + " " + currency)).shouldBe(visible);
    }

    @Step("Проверить, что лейбл с тратами по архивным категориям содержит верный текст")
    public void checkArchivedSpendsLegendLabel(Double amount, CurrencyValues cur) {
        String amountString = String.valueOf(amount.intValue());
        String currency = cur.symbol;
        legendLabels.findBy(text("Archived" + " " + amountString + " " + currency)).shouldBe(visible);
    }

}
