package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.auth.AuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.userdata.UserDaoJdbc;
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
import java.util.UUID;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class UsersDbClient {

    Connection userdataConnection = Databases.connection(CFG.userdataJdbcUrl());
    Connection authConnection = Databases.connection(CFG.authJdbcUrl());
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    private final UserDao userDao = new UserDaoJdbc(userdataConnection);

    public UsersDbClient() throws SQLException {
    }

    public UserJson createUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        return UserJson.fromEntity(
                userDao.createUser(userEntity)
        );
    }

    public UserJson findUserById(UUID id) {
        return UserJson
                .fromEntity(userDao.findUserById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserJson findUserByUsername(String username) {
        return UserJson
                .fromEntity(userDao.findUserByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public void deleteUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        userDao.deleteUser(userEntity);
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
                            .createUser(authUser);
                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    new AuthorityDaoJdbc(authConnection)
                            .createAuthority(authorityEntities);
                    return createdAuthUser;
                }, CFG.authJdbcUrl()),
                // создание пользователя в userdata
                new Databases.XaFunction<>(connection -> {
                    UserEntity createdUser = new UserDaoJdbc(connection)
                            .createUser(UserEntity.fromJson(user));
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
                    ae.setUser(authUser);
                    new AuthorityDaoJdbc(connection).deleteAuthority(ae);

                    new AuthUserDaoJdbc(connection).deleteUser(authUser);
                }, CFG.authJdbcUrl()),
                // удаление пользователя в userdata
                new Databases.XaConsumer(connection -> {
                    new UserDaoJdbc(connection).deleteUser(UserEntity.fromJson(user));
                }, CFG.userdataJdbcUrl())
        );
    }

}
