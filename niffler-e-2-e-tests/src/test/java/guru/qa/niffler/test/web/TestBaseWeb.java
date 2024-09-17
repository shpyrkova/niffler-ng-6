package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class TestBaseWeb {

    protected final RegisterPage registerPage = new RegisterPage();
    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();

}
