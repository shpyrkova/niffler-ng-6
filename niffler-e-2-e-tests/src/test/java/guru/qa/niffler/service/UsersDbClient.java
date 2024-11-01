package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepositoryHibernate = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepositoryHibernate = new UserdataUserRepositoryHibernate();

    private final AuthUserRepository authUserRepositoryJdbc = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRepositoryJdbc = new UserdataUserRepositoryJdbc();

    private final AuthUserRepository authUserRepositorySpringJdbc = new AuthUserRepositorySpringJdbc();
    private final UserdataUserRepository userdataUserRepositorySpringJdbc = new UserdataUserRepositorySpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Nonnull
    @Override
    @Step("Создать пользователя")
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
        authUserRepositoryHibernate.create(authUser);
        return userdataUserRepositoryHibernate.create(userEntity(username));
    }

    @Override
    @Step("Добавить входящее приглашение")
    public void addIncomeInvitation(UserJson addressee, int count) {
        if (count > 0) {
            UserEntity addresseeEntity = userdataUserRepositoryHibernate.findById(
                    addressee.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "00000000");
                            authUserRepositoryHibernate.create(authUser);
                            UserEntity requester = userdataUserRepositoryHibernate.create(userEntity(username));
                            userdataUserRepositoryHibernate.addInvitation(requester, addresseeEntity);
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
            UserEntity requesterEntity = userdataUserRepositoryHibernate.findById(
                    requester.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "00000000");
                            authUserRepositoryHibernate.create(authUser);
                            UserEntity addressee = userdataUserRepositoryHibernate.create(userEntity(username));
                            userdataUserRepositoryHibernate.addInvitation(requesterEntity, addressee);
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
            UserEntity addresseeEntity = userdataUserRepositoryHibernate.findById(
                    addressee.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "00000000");
                            authUserRepositoryHibernate.create(authUser);
                            UserEntity requester = userdataUserRepositoryHibernate.create(userEntity(username));
                            userdataUserRepositoryHibernate.addFriend(addresseeEntity, requester);
                            return null;
                        }
                );
            }
        }
    }

    @Step("Найти пользователя по id")
    public UserJson findUserById(UUID id) {
        return UserJson
                .fromEntity(userdataUserRepositoryHibernate.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")), null);
    }

    @Step("Найти пользователя по username")
    public UserJson findUserByUsername(String username) {
        return UserJson
                .fromEntity(userdataUserRepositoryHibernate.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")), null);
    }


    @Step("Удалить пользователя")
    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setId(UUID.fromString("b95b0d4e-904f-11ef-97a8-0242ac110004")); // пока никуда не выносили
            authUserRepositoryHibernate.remove(authUser);
            userdataUserRepositoryHibernate.remove(UserEntity.fromJson(user));
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
