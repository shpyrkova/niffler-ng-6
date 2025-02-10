package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class FriendsTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @User(friends = 2)
    @ApiLogin
    @Test
    void deleteFriendshipTest(UserJson user, @Token String token) {
        String friendToRemove = user.testData().friendsUsernames()[0];
        gatewayApiClient.removeFriend(token, friendToRemove);
        final List<UserJson> allFriends = gatewayApiClient.allFriends(
                token,
                null
        );
        assertThat(allFriends.size()).isEqualTo(1);
        assertThat(allFriends.stream().noneMatch(u -> u.username().equals(friendToRemove))).isEqualTo(true);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void acceptInvitationTest(UserJson user, @Token String token) {
        FriendJson invitation = new FriendJson(user.testData().incomeInvitationsUsernames()[0]);
        gatewayApiClient.acceptInvitation(token, invitation);
        final List<UserJson> allFriends = gatewayApiClient.allFriends(
                token,
                invitation.username()
        );
        assertThat(allFriends.size()).isEqualTo(1);
        assertThat(allFriends.getFirst().username()).isEqualTo(invitation.username());
        assertThat(allFriends.getFirst().friendshipStatus()).isEqualTo(FriendshipStatus.FRIEND);
    }

    @User(incomeInvitations = 2)
    @ApiLogin
    @Test
    void declineInvitationTest(UserJson user, @Token String token) {
        FriendJson invitationToDecline = new FriendJson(user.testData().incomeInvitationsUsernames()[0]);
        FriendJson invitation = new FriendJson(user.testData().incomeInvitationsUsernames()[1]);
        gatewayApiClient.declineInvitation(token, invitationToDecline);
        final List<UserJson> allFriends = gatewayApiClient.allFriends(
                token,
                null
        );
        assertThat(allFriends.size()).isEqualTo(1);
        assertThat(allFriends.getFirst().username()).isEqualTo(invitation.username());
    }

}