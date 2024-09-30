package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.UUID;

public class UserdataDbClient {

    private final UserDao userDao = new UserDaoJdbc();

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

}
