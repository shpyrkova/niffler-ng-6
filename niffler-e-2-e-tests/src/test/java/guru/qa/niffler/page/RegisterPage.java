package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement signUpButton = $("button[type='submit']");
    private final SelenideElement signInLink = $(byText("Sign in"));

    private SelenideElement passwordsShouldBeEqualErrorMessage() {
        return $(byText("Passwords should be equal"));
    }

    private SelenideElement userWithUsernameAlreadyExistsErrorMessage(String username) {
        return $(byText("Username" + " `" + username + "` " + "already exists"));
    }

    @Step("Заполнить логин")
    public void setUsername(String username) {
        usernameInput.setValue(username);
    }

    @Step("Заполнить пароль")
    public void setPassword(String password) {
        passwordInput.setValue(password);
    }

    @Step("Подтвердить пароль")
    public void confirmPassword(String password) {
        passwordSubmitInput.setValue(password);
    }

    @Step("Нажать Зарегистрироваться")
    public void clickSignUpButton() {
        signUpButton.click();
    }

    @Step("Зарегистрироваться с логином {username} и паролем {password}")
    public void register(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        this.confirmPassword(password);
        this.clickSignUpButton();
    }

    @Step("Нажать Войти")
    public void clickSignInLink() {
        signInLink.click();
    }

    @Step("Проверить, что показано сообщение о разных значениях пароля")
    public void passwordsShouldBeEqualErrorMessageShouldBePresent() {
        passwordsShouldBeEqualErrorMessage().shouldBe(visible);
    }

    @Step("Проверить, что показано уведомление о том, что юзер с таким ником уже существует")
    public void userWithUsernameAlreadyExistsErrorMessageShouldBePresent(String username) {
        userWithUsernameAlreadyExistsErrorMessage(username).shouldBe(visible);
    }

}
