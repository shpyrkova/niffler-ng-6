package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

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

    public void setUsername(String username) {
        usernameInput.setValue(username);
    }

    public void setPassword(String password) {
        passwordInput.setValue(password);
    }

    public void confirmPassword(String password) {
        passwordSubmitInput.setValue(password);
    }

    public void clickSignUpButton() {
        signUpButton.click();
    }

    public void register(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        this.confirmPassword(password);
        this.clickSignUpButton();
    }

    public void clickSignInLink() {
        signInLink.click();
    }

    public void passwordsShouldBeEqualErrorMessageShouldBePresent() {
        passwordsShouldBeEqualErrorMessage().shouldBe(visible);
    }

    public void userWithUsernameAlreadyExistsErrorMessageShouldBePresent(String username) {
        userWithUsernameAlreadyExistsErrorMessage(username).shouldBe(visible);
    }

}
