package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.AuthorityDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.auth.AuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.userdata.UserDaoJdbc;
import guru.qa.niffler.data.dao.impl.userdata.UserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // JDBC DAO
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthorityDao authorityDao = new AuthorityDaoJdbc();
    private final UserDao userDao = new UserDaoJdbc();

    // SPRING-JDBC DAO
    private final UserDao userDaoSpringJdbc = new UserDaoSpringJdbc();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    TransactionTemplate springChainedTxTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(dataSource(CFG.userdataJdbcUrl()))
            )
    );

    public UserJson findUserById(UUID id) {
        return UserJson
                .fromEntity(userDao.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserJson findUserByUsername(String username) {
        return UserJson
                .fromEntity(userDao.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserJson createUserSpringChainedTransaction(UserJson user) {
        return springChainedTxTemplate.execute(status -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("00000000"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserDao.create(authUser);
            AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setUser(createdAuthUser);
                        ae.setAuthority(e);
                        return ae;
                    }
            ).toArray(AuthorityEntity[]::new);

            authorityDao.create(authorityEntities);

            UserEntity createdUser = userDao.create(UserEntity.fromJson(user));
            return UserJson.fromEntity(createdUser);
        });
    }

    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    // создание пользователя и его authorities в auth
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(username);
                    authUser.setPassword(password); // пока никуда не выносили
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.create(authUser);
                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authorityDao.create(authorityEntities);
                    // создание пользователя в userdata
                    UserEntity createdUser = userdataUserRepository.create(userEntity(username));
                    return UserJson.fromEntity(createdUser);
                }
        );
    }

    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            // удаление пользователя и его authorities в auth
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setId(UUID.fromString("716193a0-80b4-11ef-81d0-0242ac110004")); // пока никуда не выносили
            AuthorityEntity ae = new AuthorityEntity();
            ae.setUser(authUser);
            authorityDao.delete(ae);

            authUserDao.delete(authUser);
            // удаление пользователя в userdata
            userDao.delete(UserEntity.fromJson(user));
        });
    }

    public List<UserJson> findAllUsers() {
        List<UserEntity> userEntities = userDao.findAll();
        return userEntities.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public List<UserJson> findAllUsersSpringJdbc() {
        List<UserEntity> userEntities = userDaoSpringJdbc.findAll();
        return userEntities.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public void addIncomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }
    public void addOutcomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addFriend(UserJson requester, UserJson addressee) throws SQLException {
        UserEntity requesterEntity = UserEntity.fromJson(requester);
        UserEntity addresseeEntity = UserEntity.fromJson(addressee);
        userRepository.addFriend(requesterEntity, addresseeEntity);
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
