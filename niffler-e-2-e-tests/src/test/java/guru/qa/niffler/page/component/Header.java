package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($("#root header"));
    }

    private final SelenideElement mainPageLink = self.$("a[href*='/main']");
    private final SelenideElement addSpendingBtn = self.$("a[href*='/spending']");
    private final SelenideElement menuBtn = self.$("button");
    private final SelenideElement menu = $("ul[role='menu']");
    private final ElementsCollection menuItems = menu.$$("li");

}