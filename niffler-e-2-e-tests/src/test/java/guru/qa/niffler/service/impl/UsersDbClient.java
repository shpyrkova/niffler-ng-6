package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Nonnull
    @Override
    @Step("Создать пользователя через БД")
    public UserJson createUser(String username, String password) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> UserJson.fromEntity(
                                createNewUser(username, password),
                                null
                        ).addTestData(
                                new TestData(
                                        password
                                )
                        )
                )
        );
    }

    @Nonnull
    private UserEntity createNewUser(String username, String password) {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
        return userdataUserRepository.create(userEntity(username));
    }

    @Override
    @Step("Добавить входящее приглашение")
    public void addIncomeInvitation(UserJson addressee, int count) {
        if (count > 0) {
            UserEntity addresseeEntity = userdataUserRepository.findById(
                    addressee.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "00000000");
                            authUserRepository.create(authUser);
                            UserEntity requester = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addInvitation(requester, addresseeEntity);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    @Step("Добавить исходящее приглашение")
    public void addOutcomeInvitation(UserJson requester, int count) {
        if (count > 0) {
            UserEntity requesterEntity = userdataUserRepository.findById(
                    requester.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "00000000");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addInvitation(requesterEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    @Step("Добавить друзей")
    public void addFriend(UserJson addressee, int count) {
        if (count > 0) {
            UserEntity addresseeEntity = userdataUserRepository.findById(
                    addressee.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "00000000");
                            authUserRepository.create(authUser);
                            UserEntity requester = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addFriend(addresseeEntity, requester);
                            return null;
                        }
                );
            }
        }
    }

    @Nonnull
    @Step("Найти пользователя по id")
    public UserJson findUserById(UUID id) {
        return UserJson
                .fromEntity(userdataUserRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")), null);
    }

    @Nonnull
    @Step("Найти пользователя по username")
    public UserJson findUserByUsername(String username) {
        return UserJson
                .fromEntity(userdataUserRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")), null);
    }


    @Step("Удалить пользователя")
    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setId(UUID.fromString("b95b0d4e-904f-11ef-97a8-0242ac110004")); // пока никуда не выносили
            authUserRepository.remove(authUser);
            userdataUserRepository.remove(UserEntity.fromJson(user));
        });
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }

}
