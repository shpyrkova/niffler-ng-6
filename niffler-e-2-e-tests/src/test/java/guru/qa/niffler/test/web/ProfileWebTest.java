package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

public class ProfileWebTest extends TestBaseWeb {

    ProfilePage profilePage = new ProfilePage();

    @User(categories = @Category(archived = false))
    @Test
    void archiveCategoryTest(UserJson user) {
        String categoryName = user.testData().categories().getFirst().name();

        loginPage.login(user.username(), user.testData().password());
        mainPage.clickProfileMenuButton();
        mainPage.clickProfileLink();
        profilePage.archiveCategory(categoryName);
        profilePage.categoryDeletedMessageHeaderShouldBePresent(categoryName);
        profilePage.checkCategoryVisibility(categoryName, false);
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(categoryName, true);
    }

    @User(categories = @Category(archived = true))
    @Test
    void restoreFromArchiveCategoryTest(UserJson user) {
        String categoryName = user.testData().categories().getFirst().name();

        loginPage.login(user.username(), user.testData().password());
        mainPage.clickProfileMenuButton();
        mainPage.clickProfileLink();
        profilePage.clickShowArchivedText();
        profilePage.restoreFromArchiveCategory(categoryName);
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(categoryName, true);
    }

}
