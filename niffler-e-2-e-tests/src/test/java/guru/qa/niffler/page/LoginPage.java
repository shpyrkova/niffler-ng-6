package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement createNewAccountLink = $(byText("Create new account"));
    private final SelenideElement invalidCredentialsErrorMessage = $(byText("Неверные учетные данные пользователя"));

    @Nonnull
    @Step("Авторизоваться с логином {username} и паролем {password}")
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Нажать Create new account")
    public RegisterPage clickCreateNewAccountLink() {
        createNewAccountLink.click();
        return new RegisterPage();
    }

    @Step("Проверить, что присутствует сообщение о неверных учетных данных")
    public void invalidCredentialsErrorMessageShouldBePresent() {
        invalidCredentialsErrorMessage.shouldBe(visible);
    }

}
