package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();
    private final SpendApi spendApi = new RestClient.EmptyClient(CFG.spendUrl()).create(SpendApi.class);

    @Override
    @Nonnull
    @Step("Создать трату")
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return requireNonNull(response.body());
    }

    @Override
    @Nonnull
    @Step("Создать категорию")
    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        CategoryJson result = requireNonNull(response.body());

        return category.archived()
                ? updateCategory(
                new CategoryJson(
                        result.id(),
                        result.name(),
                        result.username(),
                        true
                )
        ) : result;
    }

    @Override
    @Nonnull
    @Step("Обновить категорию")
    public CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.editCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Override
    @Step("Найти категорию по нику и имени категорию")
    public CategoryJson findCategoryByUsernameAndCategoryName(CategoryJson category) {
        // в internal API нет метода для поиска категории по username и name. есть только получение всех по username
        throw new UnsupportedOperationException("Can`t find category by username and category name using API");
    }

    @Override
    @Step("Удалить категорию")
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Can`t remove category using API");
    }
}
