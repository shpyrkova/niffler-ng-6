package guru.qa.niffler.utils;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.CurrencyValues;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomName() {
        return faker.name().firstName();
    }

    @Nonnull
    public static String randomCategoryName() {
        return faker.food().ingredient();
    }

    @Nonnull
    public static String randomSpendingDescription() {
        return faker.food().dish();
    }

    @Nonnull
    public static CurrencyValues randomCurrency() {
        return faker.options().option(CurrencyValues.values());
    }

    public static int randomAmount() {
        return faker.number().numberBetween(10, 500000);
    }

    @Nonnull
    public static Date randomSpendingDate() {
        Calendar from = Calendar.getInstance();
        from.set(2020, Calendar.JANUARY, 1);

        Calendar to = Calendar.getInstance();
        to.set(2024, Calendar.DECEMBER, 31);

        return faker.date().between(from.getTime(), to.getTime());
    }


}
