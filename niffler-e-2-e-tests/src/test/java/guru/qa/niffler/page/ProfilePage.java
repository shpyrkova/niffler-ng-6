package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    private final SelenideElement avatarInput = $("#image__input");
    private final SelenideElement avatarImg = $(".MuiAvatar-img");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $(byText("Save changes"));
    private final SelenideElement showArchivedText = $(byText("Show archived"));

    private SelenideElement getCategoryElement(String name) {
        return $(byText(name));
    }

    private SelenideElement categoryDeletedMessage(String name) {
        return $(byText("Category " + name + " is archived"));
    }

    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Step("Показать архивные категории")
    public void clickShowArchivedText() {
        executeJavaScript("window.scrollTo(0, 0);");
        showArchivedText.click();
    }

    @Step("Архивировать категорию")
    public void archiveCategory(String name) {
        getCategoryElement(name).parent().parent()
                .lastChild().$("[aria-label='Archive category']").click();
        $(byText("Archive")).click();
    }

    @Step("Восстановить категорию из архива")
    public void restoreFromArchiveCategory(String name) {
        getCategoryElement(name).parent().parent()
                .lastChild().$("[aria-label='Unarchive category']").click();
        $(byText("Unarchive")).click();
    }

    @Step("Проверить видимость категории в списке")
    public void checkCategoryVisibility(String categoryName, boolean shouldBeVisible) {
        if (shouldBeVisible) {
            getCategoryElement(categoryName).shouldBe(visible);
        } else {
            getCategoryElement(categoryName).shouldNotBe(visible);
        }
    }

    @Step("Проверить, что показано уведомление об удалении категории")
    public void categoryDeletedMessageHeaderShouldBePresent(String name) {
        categoryDeletedMessage(name).shouldBe(visible);
    }

    @Step("Заполнить имя")
    public void setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
    }

    @Step("Нажать Сохранить изменения")
    public void clickSaveChangesButton() {
        saveChangesButton.click();
    }

    @Step("Обновить страницу и проверить, что имя изменено")
    public void checkThatNameChanged(String name) {
        refresh();
        nameInput.shouldHave(value(name));
    }

    @Step("Проверить, что показано уведомление о сохранении изменений")
    public void checkThatProfileUpdateMessageIsPresent() {
        checkAlert("Profile successfully updated");
    }

    @Step("Загрузить аватар")
    public void uploadAvatar(String path) {
        avatarInput.uploadFromClasspath(path);
    }

    @Step("Проверить, что аватар соответствует ожидаемому")
    public void checkThatAvatarAsExpected(BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read(avatarImg.screenshot());
        assertThat(new ScreenDiffResult(actual, expected).getAsBoolean()).isFalse();
    }

}
