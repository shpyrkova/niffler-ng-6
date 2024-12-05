package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.niffler.condition.StatConditions.*;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class StatComponent extends BaseComponent<StatComponent> {
    public StatComponent() {
        super($("#stat"));
    }

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");
    private final ElementsCollection legendLabels = $("#legend-container").$$("li");

    @Step("Проверить, что пайчарт статистики соответствует ожидаемому")
    public void checkThatStatPieChartAsExpected(BufferedImage expected) throws IOException {
        sleep(2000);
        BufferedImage actual = ImageIO.read($(chart).screenshot());
        assertThat(new ScreenDiffResult(actual, expected).getAsBoolean()).isFalse();
    }

    @Step("Get screenshot of stat chart")
    @Nonnull
    public BufferedImage chartScreenshot() throws IOException {
        return ImageIO.read(requireNonNull(chart.screenshot()));
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

    @Step("Проверить содержание баблов с тратами")
    @Nonnull
    public StatComponent checkBubbles(Bubble... expectedBubbles) {
        bubbles.should(colorAndTextAnyOrder(expectedBubbles));
        return this;
    }

    @Step("Проверить, что баблы с тратами содержат ожидаемый бабл")
    @Nonnull
    public StatComponent checkBubblesContains(Bubble... expectedBubbles) {
        bubbles.should(statBubblesContains(expectedBubbles));
        return this;
    }

}