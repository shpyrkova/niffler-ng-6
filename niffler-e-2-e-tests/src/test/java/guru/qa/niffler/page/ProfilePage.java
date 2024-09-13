package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class ProfilePage {
    private final SelenideElement avatarInput = $("#image__input");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $(byText("Save changes"));
    private final SelenideElement showArchivedText = $(byText("Show archived"));

    public SelenideElement getCategoryElement(String name) {
        return $(byText(name));
    }

    public void clickShowArchivedText() {
        executeJavaScript("window.scrollTo(0, 0);");
        showArchivedText.click();
    }

    public void archiveCategory(String name) {
        getCategoryElement(name).parent().parent()
                .lastChild().$("[aria-label='Archive category']").click();
        $(byText("Archive")).click();
    }

    public SelenideElement categoryDeletedMessage(String name) {
        return $(byText("Category " + name + " is archived"));
    }

    public void restoreFromArchiveCategory(String name) {
        getCategoryElement(name).parent().parent()
                .lastChild().$("[aria-label='Unarchive category']").click();
        $(byText("Unarchive")).click();
    }

}
