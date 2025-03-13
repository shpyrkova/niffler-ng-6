package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.model.rest.pageable.RestResponsePage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiV2Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;


@RestTest
public class FriendsV2Test {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayApiV2Client gatewayApiV2Client = new GatewayApiV2Client();

    @User(friends = 2, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeFilteredByUsername(UserJson user, @Token String token) {
        final String expectedFriendUsername = user.testData().friendsUsernames()[0];
        final RestResponsePage<UserJson> filterByFriendUsernameResult = gatewayApiV2Client.allFriends(
                token,
                expectedFriendUsername,
                0,
                "username,ASC"
        );
        assertThat(filterByFriendUsernameResult.getContent().size()).isEqualTo(1);
        assertThat(filterByFriendUsernameResult.getContent().getFirst().username()).isEqualTo(expectedFriendUsername);
    }

    @User(outcomeInvitations = 1)
    @ApiLogin
    @Test
    void invitationShouldBePresentForBothUsers(UserJson user, @Token String token) {
        final String outcomeInvitationUsername = user.testData().outcomeInvitationsUsernames()[0];
        final RestResponsePage<UserJson> allUsers = gatewayApiV2Client.allUsers(
                token,
                outcomeInvitationUsername,
                0,
                "username,ASC"
        );
        assertThat(allUsers.getContent().getFirst().username()).isEqualTo(outcomeInvitationUsername);
        assertThat(allUsers.getContent().getFirst().friendshipStatus()).isEqualTo(FriendshipStatus.INVITE_SENT);

        // проверяем наличие входящего приглашения у второго юзера
        final AuthApiClient authApiClient = new AuthApiClient();
        ThreadSafeCookieStore.INSTANCE.removeAll();
        final String outcomeInvitationUserToken = authApiClient.login(outcomeInvitationUsername, "12345");
        final RestResponsePage<UserJson> allFriends = gatewayApiV2Client.allFriends(
                "Bearer " + outcomeInvitationUserToken,
                user.username(),
                0,
                "username,ASC"
        );
        assertThat(allFriends.getContent().getFirst().username()).isEqualTo(user.username());
        assertThat(allFriends.getContent().getFirst().friendshipStatus()).isEqualTo(FriendshipStatus.INVITE_RECEIVED);
    }

}