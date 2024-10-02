package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.AuthorityDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.auth.AuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.auth.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.auth.AuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.userdata.UserDaoJdbc;
import guru.qa.niffler.data.dao.impl.userdata.UserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // JDBC DAO
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthorityDao authorityDao = new AuthorityDaoJdbc();
    private final UserDao userDao = new UserDaoJdbc();

    // SPRING-JDBC DAO
    private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
    private final AuthorityDao authorityDaoSpringJdbc = new AuthorityDaoSpringJdbc();
    private final UserDao userDaoSpringJdbc = new UserDaoSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        return UserJson.fromEntity(
                userDao.create(userEntity)
        );
    }

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

    public void deleteUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        userDao.delete(userEntity);
    }

    public UserJson createUserdataAndAuthUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    // создание пользователя и его authorities в auth
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
                                ae.setUserId(createdAuthUser);
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

    public void deleteUserdataAndAuthUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            // удаление пользователя и его authorities в auth
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setId(user.authId());
            AuthorityEntity ae = new AuthorityEntity();
            ae.setUserId(authUser);
            authorityDao.delete(ae);

            authUserDao.delete(authUser);
            // удаление пользователя в userdata
            userDao.delete(UserEntity.fromJson(user));
        });
    }

    public List<UserJson> findAllUsers() throws SQLException {
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
