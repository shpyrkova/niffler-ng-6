package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @NotNull
    @Override
    @Step("Создать трату")
    public SpendJson createSpend(SpendJson spend) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
                    spendRepository.create(SpendEntity.fromJson(spend));
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    return SpendJson.fromEntity(spendEntity);
                }
        ));
    }

    @NotNull
    @Override
    @Step("Создать категорию")
    public CategoryJson createCategory(CategoryJson category) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            CategoryEntity createdCategoryEntity = spendRepository.createCategory(categoryEntity);
            return CategoryJson.fromEntity(createdCategoryEntity);
        }));
    }

    @NotNull
    @Override
    @Step("Обновить категорию")
    public CategoryJson updateCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.updateCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }

    @Override
    @Step("Найти категорию по нику и имени категорию")
    public CategoryJson findCategoryByUsernameAndCategoryName(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
            Optional<CategoryEntity> foundCategory = spendRepository
                    .findCategoryByUsernameAndCategoryName(category.username(), category.name());
            return CategoryJson
                    .fromEntity(foundCategory
                            .orElseThrow(() -> new RuntimeException("Category not found")));
        });
    }

    @Override
    @Step("Удалить категорию")
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }

    @Nonnull
    public SpendJson findSpendById(UUID id) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            Optional<SpendEntity> foundSpend = spendRepository
                    .findById(id);
            return SpendJson
                    .fromEntity(foundSpend
                            .orElseThrow(() -> new RuntimeException("Spend not found")));
        }));
    }

    @Step("Удалить трату")
    public void removeSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(SpendEntity.fromJson(spend));
        });
    }

}