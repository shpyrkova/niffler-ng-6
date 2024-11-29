package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends TestBaseWeb {

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUserTest(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toFriendsPage();
        friendsPage.checkThatNoFriendsMessageIsPresent();
    }

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTableTest(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toFriendsPage();
        friendsPage.checkThatFriendRowIsPresent(user.testData().friendsUsernames()[0]);
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTableTest(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toFriendsPage();
        friendsPage.checkThatIncomeRequestIsPresent(user.testData().incomeInvitationsUsernames()[0]);
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTableTest(UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toAllPeoplesPage();
        peoplePage.checkThatOutcomeRequestIsPresent(user.testData().outcomeInvitationsUsernames()[0]);
    }

    @User(incomeInvitations = 1)
    @Test
    void acceptIncomeInvitationTest(UserJson user) {
        String requesterUsername = user.testData().incomeInvitationsUsernames()[0];
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toFriendsPage();
        friendsPage.acceptIncomeInvitation(requesterUsername);
        friendsPage.checkThatIncomeRequestIsAbsent(requesterUsername);
        friendsPage.checkThatFriendRowIsPresent(requesterUsername);
    }

    @User(incomeInvitations = 1)
    @Test
    void declineIncomeInvitationTest(UserJson user) {
        String requesterUsername = user.testData().incomeInvitationsUsernames()[0];
        loginPage.login(user.username(), user.testData().password());
        mainPage.getHeader().toFriendsPage();
        friendsPage.declineIncomeInvitation(requesterUsername);
        friendsPage.checkThatIncomeRequestIsAbsent(requesterUsername);
    }

}
