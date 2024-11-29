package guru.qa.niffler.test.web;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

@Disabled
public class JdbcTest {

    UsersDbClient usersDbClient = new UsersDbClient();
    SpendDbClient spendDbClient = new SpendDbClient();

    @Test
    void createSpendTest() {
        SpendJson spendJson = new SpendJson(null,
                new Date(),
                new CategoryJson(null, randomCategoryName(), "giraffe", true),
                CurrencyValues.EUR,
                150.0,
                "oloo",
                "giraffe");
        System.out.println(spendDbClient.createSpend(spendJson));
    }

    @Test
    void findCategoryByUsernameAndCategoryNameTest() {
        CategoryJson category = new CategoryJson(null, "seven eleven", "dasha", false);
        System.out.println(spendDbClient.findCategoryByUsernameAndCategoryName(category));
    }

    @Test
    void createUserTest() {
        UserJson user = usersDbClient.createUser("petr-17", "00000000");
        System.out.println(user);
    }

//    @Test
//    void deleteUserHibernateTest() {
//        usersDbClient.deleteUser(
//                new UserJson(
//                        UUID.fromString("b95c5bc2-904f-11ef-b535-0242ac110004"),
//                        "petr-12"
//                )
//        );
//    }
//
//    @Test
//    void createInvitationHibernateTest() {
//        UserJson addressee = new UserJson(UUID.fromString("30d0b6a9-91b2-4519-b43a-6b4d4943c42b"), "petr-20", null, null, null, null, null, null);
//        UserJson requester = new UserJson(UUID.fromString("62502cb9-2c0e-49fd-bb85-446478fb7398"), "petr-19", null, null, null, null, null, null);
//        usersDbClient.addIncomeInvitation(addressee, 1);
//        usersDbClient.addOutcomeInvitation(requester, 1);
//    }
//
//    @Test
//    void createFriendTest() {
//        UserJson addressee = new UserJson(UUID.fromString("8fc6b029-ab3b-4545-a29e-c9ea7ec27cb1"), "petr-23", null, null, null, null, null, null);
//        usersDbClient.addFriend(addressee, 1);
//    }

    @Test
    void findUserById() {
        UserJson user = usersDbClient.findUserById(UUID.fromString("b69de36e-8065-11ef-8717-0242ac110004"));
        System.out.println(user);
    }

    @Test
    void findUserByUsername() {
        UserJson user = usersDbClient.findUserByUsername("petr-22");
        System.out.println(user);
    }

}
