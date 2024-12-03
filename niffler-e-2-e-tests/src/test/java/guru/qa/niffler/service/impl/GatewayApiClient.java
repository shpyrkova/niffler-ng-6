package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Отправить запрос GET /api/friends/all к niffler-gateway")
    public List<UserJson> allFriends(@Nonnull String bearerToken,
                                     @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = gatewayApi.allFriends(bearerToken, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Отправить запрос DELETE /api/friends/remove к niffler-gateway")
    public void removeFriend(@Nonnull String bearerToken,
                             @Nonnull String username) {
        final Response<Void> response;
        try {
            response = gatewayApi.removeFriend(bearerToken, username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Step("Отправить запрос POST api/invitations/accept к niffler-gateway")
    public UserJson acceptInvitation(@Nonnull String bearerToken,
                                 @Nonnull FriendJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.acceptInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Отправить запрос POST api/invitations/decline к niffler-gateway")
    public UserJson declineInvitation(@Nonnull String bearerToken,
                                     @Nonnull FriendJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.declineInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

}