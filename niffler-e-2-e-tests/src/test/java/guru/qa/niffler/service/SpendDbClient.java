package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
                    spendRepository.create(SpendEntity.fromJson(spend));
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    return SpendJson.fromEntity(spendEntity);
                }
        );
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            CategoryEntity createdCategoryEntity = spendRepository.createCategory(categoryEntity);
            return CategoryJson.fromEntity(createdCategoryEntity);
        });
    }

    @NotNull
    @Override
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
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }

    public SpendJson findSpendById(UUID id) {
        return xaTransactionTemplate.execute(() -> {
            Optional<SpendEntity> foundSpend = spendRepository
                    .findById(id);
            return SpendJson
                    .fromEntity(foundSpend
                            .orElseThrow(() -> new RuntimeException("Spend not found")));
        });
    }

    public void removeSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(SpendEntity.fromJson(spend));
        });
    }

}