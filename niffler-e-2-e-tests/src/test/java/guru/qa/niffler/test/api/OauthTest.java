package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OauthTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Test
    void oauthTest(UserJson user) {
        authApiClient.login(user.username(), user.testData().password());
        String code = authApiClient.authorize();
        String token = authApiClient.token(code);
        assertThat(token).isNotEmpty();
    }

}
