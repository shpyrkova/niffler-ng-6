package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@WebTest
public class OauthTest {

    @Test
    @User
    @ApiLogin
    void oauthTest(@Token String token, UserJson userJson) {
        System.out.println(userJson);
        assertThat(token).isNotEmpty();
    }

}
