package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.*;

public class ProfileWebTest extends TestBaseWeb {

    @User(categories = @Category(archived = false))
    @ApiLogin()
    @Test
    void archiveCategoryTest(UserJson user) {
        String categoryName = user.testData().categories().getFirst().name();
        profilePage.open();
        profilePage.archiveCategory(categoryName);
        profilePage.categoryDeletedMessageHeaderShouldBePresent(categoryName);
        profilePage.checkCategoryVisibility(categoryName, false);
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(categoryName, true);
    }

    @User(categories = @Category(archived = true))
    @ApiLogin
    @Test
    void restoreFromArchiveCategoryTest(UserJson user) {
        String categoryName = user.testData().categories().getFirst().name();
        profilePage.open();
        profilePage.clickShowArchivedText();
        profilePage.restoreFromArchiveCategory(categoryName);
        profilePage.clickShowArchivedText();
        profilePage.checkCategoryVisibility(categoryName, true);
    }

    @User
    @ApiLogin
    @Test
    void editProfileTest() {
        String name = randomName();
        profilePage.open();
        profilePage.setName(name);
        profilePage.clickSaveChangesButton();
        profilePage.checkThatProfileUpdateMessageIsPresent();
        profilePage.checkThatNameChanged(name);
    }

    @ScreenShotTest(value = "img/expected-avatar-test.png", rewriteExpected = true)
    @ApiLogin
    @User
    void addAvatarTest(BufferedImage expected) throws IOException {
        profilePage.open();
        profilePage.uploadAvatar("img/renoire.jpeg");
        profilePage.clickSaveChangesButton();
        profilePage.checkThatProfileUpdateMessageIsPresent();
        profilePage.checkThatAvatarAsExpected(expected);
    }

}
