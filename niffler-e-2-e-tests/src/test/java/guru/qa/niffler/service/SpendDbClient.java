package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.spend.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.spend.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spend.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.spend.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.*;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                                .create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                },
                CFG.spendJdbcUrl(), 2
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(
                            new CategoryDaoJdbc(connection).create(categoryEntity));
                },
                CFG.spendJdbcUrl(), 2
        );
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(CategoryJson category) {
        return transaction(connection -> {
                    return CategoryJson
                            .fromEntity(new CategoryDaoJdbc(connection)
                                    .findCategoryByUsernameAndCategoryName(category.username(), category.name())
                                    .orElseThrow(() -> new RuntimeException("Category not found")));
                },
                CFG.spendJdbcUrl(), 2
        );
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return transaction(connection -> {
            List<CategoryEntity> categoryEntities = new CategoryDaoJdbc(connection).findAllByUsername(username);
            return categoryEntities.stream()
                    .map(CategoryJson::fromEntity)
                    .toList();
        }, CFG.spendJdbcUrl(), 2);
    }

    public void deleteCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        transaction(connection -> {
            new CategoryDaoJdbc(connection).deleteCategory(categoryEntity);
        }, CFG.spendJdbcUrl(), 2);
    }

    public SpendJson findSpendById(UUID id) {
        return transaction(connection -> {
            SpendEntity spendEntity = new SpendDaoJdbc(connection).findById(id)
                    .orElseThrow(() -> new RuntimeException("Spend not found"));
            // заполняем данные по категории именно здесь, так как в DAO мы должны работать только с одной таблицей
            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).
                    findCategoryById(spendEntity.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            spendEntity.setCategory(categoryEntity);
            return SpendJson.fromEntity(spendEntity);
        }, CFG.spendJdbcUrl(), 2);
    }

    public List<SpendJson> findAllSpendingsByUsername(String username) {
        return transaction(connection -> {
            List<SpendEntity> spendEntities = new SpendDaoJdbc(connection).findAllByUsername(username);
            spendEntities.forEach(se -> se.setCategory(new CategoryDaoJdbc(connection).
                    findCategoryById(se.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"))));
            return spendEntities.stream()
                    .map(SpendJson::fromEntity)
                    .toList();
        }, CFG.spendJdbcUrl(), 2);
    }

    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        transaction(connection -> {
            new SpendDaoJdbc(connection).delete(spendEntity);
        }, CFG.spendJdbcUrl(), 2);
    }

    public List<SpendJson> findAllSpendings() throws SQLException {
        List<SpendEntity> spendEntities = new SpendDaoJdbc(Databases.connection(CFG.spendJdbcUrl())).findAll();
        return spendEntities.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public List<SpendJson> findAllSpendingsSpringJdbc() {
        List<SpendEntity> spendEntities = new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll();
        return spendEntities.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public List<CategoryJson> findAllCategories() throws SQLException {
        List<CategoryEntity> categoryEntities = new CategoryDaoJdbc(Databases.connection(CFG.spendJdbcUrl())).findAll();
        return categoryEntities.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

    public List<CategoryJson> findAllCategoriesSpringJdbc() {
        List<CategoryEntity> categoryEntities = new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll();
        return categoryEntities.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

}