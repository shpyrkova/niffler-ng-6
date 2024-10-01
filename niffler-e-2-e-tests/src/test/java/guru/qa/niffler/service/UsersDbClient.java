package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
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
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();

    Connection userdataConnection = Databases.connection(CFG.userdataJdbcUrl());
    Connection authConnection = Databases.connection(CFG.authJdbcUrl());
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final UserDao userDao = new UserDaoJdbc(userdataConnection);

    public UsersDbClient() throws SQLException {
    }

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
                .fromEntity(userDao.findUserByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public void deleteUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        userDao.delete(userEntity);
    }

    public UserJson createUserdataAndAuthUser(UserJson user) {
        return (UserJson) xaTransaction(2,
                // создание пользователя и его authorities в auth
                new Databases.XaFunction<>(connection -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("00000000"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = new AuthUserDaoJdbc(connection)
                            .create(authUser);
                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUserId(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    new AuthorityDaoJdbc(authConnection)
                            .create(authorityEntities);
                    return createdAuthUser;
                }, CFG.authJdbcUrl()),
                // создание пользователя в userdata
                new Databases.XaFunction<>(connection -> {
                    UserEntity createdUser = new UserDaoJdbc(connection)
                            .create(UserEntity.fromJson(user));
                    return UserJson.fromEntity(createdUser);
                }, CFG.userdataJdbcUrl())
        );
    }

    public void deleteUserdataAndAuthUser(UserJson user) {
        xaTransaction(2,
                // удаление пользователя и его authorities в auth
                new Databases.XaConsumer(connection -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setId(user.authId());
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(authUser);
                    new AuthorityDaoJdbc(connection).deleteAuthority(ae);

                    new AuthUserDaoJdbc(connection).delete(authUser);
                }, CFG.authJdbcUrl()),
                // удаление пользователя в userdata
                new Databases.XaConsumer(connection -> {
                    new UserDaoJdbc(connection).delete(UserEntity.fromJson(user));
                }, CFG.userdataJdbcUrl())
        );
    }

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authorityEntities);

        return UserJson.fromEntity(
                new UserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                        .create(
                                UserEntity.fromJson(user)
                        )
        );
    }

    public List<UserJson> findAllUsers() throws SQLException {
        List<UserEntity> userEntities = new UserDaoJdbc(Databases.connection(CFG.userdataJdbcUrl())).findAll();
        return userEntities.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public List<UserJson> findAllUsersSpringJdbc() {
        List<UserEntity> userEntities = new UserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl())).findAll();
        return userEntities.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

}
