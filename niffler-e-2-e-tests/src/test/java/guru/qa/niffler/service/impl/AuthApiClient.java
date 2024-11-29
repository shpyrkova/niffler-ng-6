package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import lombok.SneakyThrows;
import retrofit2.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static guru.qa.niffler.utils.OAuthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OAuthUtils.generateCodeVerifier;

public class AuthApiClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);

    private final String clientId = "client";
    private final String redirectUri = CFG.frontUrl() + "authorized";
    private final String codeVerifier = generateCodeVerifier();
    private final String codeChallenge;

    {
        try {
            codeChallenge = generateCodeChallenge(codeVerifier);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public String authorize() {
        final String responseType = "code";
        final String scope = "openid";
        final String codeChallengeMethod = "S256";
        final Response<Response<Void>> response;
        try {
            response = authApi
                    .authorize(responseType, clientId, scope, redirectUri, codeChallenge, codeChallengeMethod)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        String location = response.headers().get("Location");
        return StringUtils.substringAfter(location, "code=");
    }

    @SneakyThrows
    public String token(String code) {
        final Response<JsonNode> response;
        final String grantType = "authorization_code";
        try {
            response = authApi.token(clientId, redirectUri, grantType, code, codeVerifier)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return response.body().get("id_token").toString();
    }

    @SneakyThrows
    public void login(String username,
                      String password) {
        try {
            authApi.login(username, password, ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN"))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
