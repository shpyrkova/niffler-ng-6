package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@ExtendWith(BrowserExtension.class)
public class ProfileWebTest {

    private static final Config CFG = Config.getInstance();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    ProfilePage profilePage = new ProfilePage();

    @BeforeEach
    void beforeEach() {
        Selenide.open(CFG.frontUrl());
    }

    @AfterEach
    void afterEach() {
        closeWebDriver();
    }

    @Category(username = "dasha", archived = false)
    @Test
    void archiveCategoryTest(CategoryJson category) {
        loginPage.login("dasha", "00000000");
        mainPage.clickProfileMenuButton();
        mainPage.clickProfileLink();
        profilePage.archiveCategory(category.name());
        profilePage.categoryDeletedMessageHeaderShouldBePresent(category.name());
        profilePage.checkCategoryVisibility(category.name(), false);
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(category.name(), true);
    }

    @Category(username = "dasha", archived = true)
    @Test
    void restoreFromArchiveCategoryTest(CategoryJson category) {
        loginPage.login("dasha", "00000000");
        mainPage.clickProfileMenuButton();
        mainPage.clickProfileLink();
        profilePage.clickShowArchivedText();
        profilePage.restoreFromArchiveCategory(category.name());
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(category.name(), true);
    }

}
