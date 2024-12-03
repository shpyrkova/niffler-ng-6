package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RestTest
@Order(1)
public class GetEmptyUserListTest {

    private static final Config CFG = Config.getInstance();
    private final UserdataApi userdataApi = new RestClient.EmptyClient(CFG.userdataUrl()).create(UserdataApi.class);

    @User
    @Test
    void getEmptyUsersListTest(UserJson user) throws IOException {
        List<UserJson> allUsers = userdataApi.allUsers(user.username(), "").execute().body();
        assertThat(allUsers).isEmpty();
    }

}