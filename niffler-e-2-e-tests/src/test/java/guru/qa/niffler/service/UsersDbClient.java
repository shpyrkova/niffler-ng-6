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
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // JDBC DAO
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthorityDao authorityDao = new AuthorityDaoJdbc();
    private final UserDao userDao = new UserDaoJdbc();

    // SPRING-JDBC DAO
    private final UserDao userDaoSpringJdbc = new UserDaoSpringJdbc();

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

    public UserJson createUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    // создание пользователя и его authorities в auth
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("00000000")); // пока никуда не выносили
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
                    UserEntity createdUser = userDao.create(UserEntity.fromJson(user));
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

}
