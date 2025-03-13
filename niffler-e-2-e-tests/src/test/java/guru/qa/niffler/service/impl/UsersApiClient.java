package guru.qa.niffler.service.impl;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient.EmptyClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private final AuthApi authApi = new EmptyClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataApi userdataApi = new EmptyClient(CFG.userdataUrl()).create(UserdataApi.class);

    @Nonnull
    @Override
    @Step("Создать пользователя через API")
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();

            Stopwatch sw = Stopwatch.createStarted();
            long maxWaitTime = 5000L; // время ожидания - 5 секунд
            while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
                UserJson userJson = userdataApi.currentUser(username).execute().body();
                if (userJson != null && userJson.id() != null) {
                    return userJson.addTestData(
                            new TestData(
                                    password
                            ));
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Пользователь не найден в userdata по истечении времени ожидания");
    }

    @Override
    @Step("Добавить входящее приглашение")
    public void addIncomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);

                    response = userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .incomeInvitations()
                        .add(newUser);
            }
        }
    }

    @Override
    @Step("Добавить исходящее приглашение")
    public void addOutcomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);

                    response = userdataApi.sendInvitation(
                            targetUser.username(),
                            newUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .outcomeInvitations()
                        .add(newUser);
            }
        }
    }

    @Override
    @Step("Добавить друзей")
    public void addFriend(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                try {
                    userdataApi.sendInvitation(
                            createUser(
                                    username,
                                    defaultPassword
                            ).username(),
                            targetUser.username()
                    ).execute();
                    response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .friends()
                        .add(response.body());
            }
        }
    }

    @Step("Получить всех друзей по username")
    public List<UserJson> getAllFriends(UserJson user) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.friends(user.username(), null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body().stream()
                .filter(u -> FriendshipStatus.FRIEND.equals(u.friendshipStatus())).collect(Collectors.toList()));
    }

    @Step("Получить все входящие приглашения по username")
    public List<UserJson> getAllIncomeInvitations(UserJson user) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.friends(user.username(), null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body().stream()
                .filter(u -> FriendshipStatus.INVITE_RECEIVED.equals(u.friendshipStatus())).collect(Collectors.toList()));
    }

    @Step("Получить все исходящие приглашения по username")
    public List<UserJson> getAllOutcomeInvitations(UserJson user) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.allUsers(user.username(), null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body().stream()
                .filter(u -> FriendshipStatus.INVITE_SENT.equals(u.friendshipStatus())).collect(Collectors.toList()));
    }

}