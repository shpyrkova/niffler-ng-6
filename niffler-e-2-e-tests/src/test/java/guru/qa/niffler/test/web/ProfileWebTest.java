package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.*;

public class ProfileWebTest extends TestBaseWeb {

    @User(categories = @Category(archived = false))
    @Test
    void archiveCategoryTest(UserJson user) {
        String categoryName = user.testData().categories().getFirst().name();

        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toProfilePage();
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
        mainPage.getHeader().toProfilePage();
        profilePage.clickShowArchivedText();
        profilePage.restoreFromArchiveCategory(categoryName);
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(categoryName, true);
    }

    @User
    @Test
    void editProfileTest(UserJson user) {
        String name = randomName();
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toProfilePage();
        profilePage.setName(name);
        profilePage.clickSaveChangesButton();
        profilePage.checkThatProfileUpdateMessageIsPresent();
        profilePage.checkThatNameChanged(name);
    }

}
