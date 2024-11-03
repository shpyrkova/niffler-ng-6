package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage extends BasePage<PeoplePage> {

    private final ElementsCollection allPeopleTableRows = $("#all").$$("tr");

    @Step("Проверить, что присутствует строка с исходящим приглашением {username}")
    public void checkThatOutcomeRequestIsPresent(String username) {
        allPeopleTableRows.find(text(username)).shouldBe(visible).shouldHave(text("Waiting..."));
    }

}
