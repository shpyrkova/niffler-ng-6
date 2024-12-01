package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();
    protected final SelenideElement alert = $("div[role='alert']");

    public void checkAlert(String message) {
        alert.shouldHave(text(message));
    }

}