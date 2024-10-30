package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement clearSearchInputButton = $("#input-clear");

    public SearchField() {
        super($("[placeholder='Search']"));
    }

    @Step("Ввести в поиске {query} и нажать Enter")
    public void search(String query) {
        clearIfNotEmpty();
        self.setValue(query);
        self.pressEnter();
    }

    private void clearIfNotEmpty() {
        if (self.is(not(empty))) {
            clearSearchInputButton.click();
        }
    }

}
