package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement saveBtn = $("#save");

    @Nonnull
    @Step("Указать описание {description}")
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Изменить стоимость на {amount}")
    public EditSpendingPage setNewSpendingAmount(String amount) {
        amountInput.clear();
        amountInput.setValue(amount);
        return this;
    }

    @Step("Сохранить")
    public void save() {
        saveBtn.click();
    }

}
