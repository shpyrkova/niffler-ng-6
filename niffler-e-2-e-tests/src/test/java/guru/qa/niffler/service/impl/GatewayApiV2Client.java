package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.model.rest.pageable.RestResponsePage;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiV2Client extends RestClient {

    private final GatewayV2Api gatewayV2Api;

    public GatewayApiV2Client() {
        super(CFG.gatewayUrl());
        this.gatewayV2Api = create(GatewayV2Api.class);
    }

    @Step("Отправить запрос GET /api/v2/friends/all к niffler-gateway")
    public RestResponsePage<UserJson> allFriends(@Nonnull String bearerToken,
                                                 @Nullable String searchQuery,
                                                 int page,
                                                 @Nullable String sort) {
        final Response<RestResponsePage<UserJson>> response;
        try {
            response = gatewayV2Api.allFriends(bearerToken, searchQuery, page, sort).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Отправить запрос GET /api/v2/users/all к niffler-gateway")
    public RestResponsePage<UserJson> allUsers(@Nonnull String bearerToken,
                                                 @Nullable String searchQuery,
                                                 int page,
                                                 @Nullable String sort) {
        final Response<RestResponsePage<UserJson>> response;
        try {
            response = gatewayV2Api.allUsers(bearerToken, searchQuery, page, sort).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

}