package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class JdbcTest {

    // Spring-JDBC
    @Test
    void testFindAllUdUsersSpring() {
        UsersDbClient usersDbClient = new UsersDbClient();
        System.out.println(usersDbClient.findAllUsersSpringJdbc());
    }

    // JDBC no Tx
    @Test
    void testFindAllUdUsers() {
        UsersDbClient usersDbClient = new UsersDbClient();
        System.out.println(usersDbClient.findAllUsers());
    }

    // JDBC Tx
    @Test
    void createSpendingTxJdbc() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spendJson = new SpendJson(null,
                new Date(),
                new CategoryJson(null, randomCategoryName(), "giraffe", true),
                CurrencyValues.EUR,
                150.0,
                "oloo",
                "giraffe");
        System.out.println(spendDbClient.createSpend(spendJson));
    }

    // JDBC + XaTx
    @Test
    void createUserJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.createUser(
                new UserJson(
                        null,
                        "valentin-120",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    // Spring-JDBC Tx
    /* Задание 2. Доказать возможность / невозможность отката внутренней транзакции при сбое во внешней, с применением ChainedTransactionManager
    Если падает вторая транзакция в ChainedTransactionManager, то первая транзакция не откатывается, проверено
    * */
    @Test
    void failedChainedTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.createUserSpringChainedTransaction(
                new UserJson(
                        null,
                        "petr5",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void deleteUserJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.deleteUser(
                new UserJson(
                        UUID.fromString("7163fa32-80b4-11ef-9221-0242ac110004"),
                        "valentin-12",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
    }

    @Test
    void findUserById() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.findUserById(UUID.fromString("b69de36e-8065-11ef-8717-0242ac110004"));
        System.out.println(user);
    }

}
