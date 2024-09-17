package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement createNewAccountLink = $(byText("Create new account"));

    private SelenideElement invalidCredentialsErrorMessage() {
        return $(byText("Неверные учетные данные пользователя"));
    }

    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    public RegisterPage clickCreateNewAccountLink() {
        createNewAccountLink.click();
        return new RegisterPage();
    }

    public void invalidCredentialsErrorMessageShouldBePresent() {
        invalidCredentialsErrorMessage().shouldBe(visible);
    }

}
