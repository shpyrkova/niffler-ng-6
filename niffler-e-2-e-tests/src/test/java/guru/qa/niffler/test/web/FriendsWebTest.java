package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends TestBaseWeb {

    @User
    @ApiLogin
    @Test
    void friendsTableShouldBeEmptyForNewUserTest() {
        friendsPage.open();
        friendsPage.checkThatNoFriendsMessageIsPresent();
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void friendShouldBePresentInFriendsTableTest(UserJson user) {
        friendsPage.open();
        friendsPage.checkThatFriendRowIsPresent(user.testData().friendsUsernames()[0]);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void incomeInvitationBePresentInFriendsTableTest(UserJson user) {
        friendsPage.open();
        friendsPage.checkThatIncomeRequestIsPresent(user.testData().incomeInvitationsUsernames()[0]);
    }

    @User(outcomeInvitations = 1)
    @ApiLogin
    @Test
    void outcomeInvitationBePresentInAllPeoplesTableTest(UserJson user) {
        friendsPage.open();
        friendsPage.clickAllPeopleTab();
        peoplePage.checkThatOutcomeRequestIsPresent(user.testData().outcomeInvitationsUsernames()[0]);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void acceptIncomeInvitationTest(UserJson user) {
        String requesterUsername = user.testData().incomeInvitationsUsernames()[0];
        friendsPage.open();
        friendsPage.acceptIncomeInvitation(requesterUsername);
        friendsPage.checkThatIncomeRequestIsAbsent(requesterUsername);
        friendsPage.checkThatFriendRowIsPresent(requesterUsername);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void declineIncomeInvitationTest(UserJson user) {
        String requesterUsername = user.testData().incomeInvitationsUsernames()[0];
        friendsPage.open();
        friendsPage.declineIncomeInvitation(requesterUsername);
        friendsPage.checkThatIncomeRequestIsAbsent(requesterUsername);
    }

}
