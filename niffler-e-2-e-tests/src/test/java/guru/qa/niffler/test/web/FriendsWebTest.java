package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.EMPTY;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_FRIEND;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_OUTCOME_REQUEST;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_INCOME_REQUEST;

@ExtendWith(UsersQueueExtension.class)
public class FriendsWebTest extends TestBaseWeb {

    FriendsPage friendsPage = new FriendsPage();

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.noFriendsMessageShouldBePresent();
    }

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.friendRowShouldBePresent(user.friend());
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.incomeRequestShouldBePresent(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.clickAllPeopleTab();
        friendsPage.outcomeRequestShouldBePresent(user.outcome());
    }

}
