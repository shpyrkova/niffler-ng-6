package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

public class ProfileWebTest extends TestBaseWeb {

    ProfilePage profilePage = new ProfilePage();

    @User(username = "dasha",
            categories = @Category(archived = false))
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

    @User(username = "dasha",
            categories = @Category(archived = true))
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
