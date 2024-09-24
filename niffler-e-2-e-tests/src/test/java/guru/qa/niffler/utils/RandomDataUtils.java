package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static String randomUsername() {
        return faker.internet().domainWord();
    }

    public static String randomCategoryName() {
        return faker.food().ingredient();
    }
}
