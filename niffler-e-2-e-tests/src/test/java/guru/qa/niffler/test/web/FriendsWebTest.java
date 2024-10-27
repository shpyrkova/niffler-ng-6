package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends TestBaseWeb {

    FriendsPage friendsPage = new FriendsPage();

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.checkThatNoFriendsMessageIsPresent();
    }

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.checkThatFriendRowIsPresent(user.testData().friendsUsernames()[0]);
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.checkThatIncomeRequestIsPresent(user.testData().incomeInvitationsUsernames()[0]);
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.clickProfileMenuButton();
        mainPage.clickFriendsLink();
        friendsPage.clickAllPeopleTab();
        friendsPage.checkThatOutcomeRequestIsPresent(user.testData().outcomeInvitationsUsernames()[0]);
    }

}
