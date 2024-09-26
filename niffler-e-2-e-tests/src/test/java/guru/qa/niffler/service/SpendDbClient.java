package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(
                spendDao.create(spendEntity)
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return CategoryJson.fromEntity(
                categoryDao.create(categoryEntity)
        );
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(CategoryJson category) {
        return CategoryJson
                .fromEntity(categoryDao.findCategoryByUsernameAndCategoryName(category.username(), category.name())
                        .orElseThrow(() -> new RuntimeException("Category not found")));
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        List<CategoryEntity> categoryEntities = categoryDao.findAllByUsername(username);
        return categoryEntities.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

    public void deleteCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        categoryDao.deleteCategory(categoryEntity);
    }

    public SpendJson findSpendById(UUID id) {
        SpendEntity spendEntity = spendDao.findSpendById(id)
                .orElseThrow(() -> new RuntimeException("Spend not found"));
        // заполняем данные по категории именно здесь, так как в DAO мы должны работать только с одной таблицей
        CategoryEntity categoryEntity = categoryDao.
                findCategoryById(spendEntity.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        spendEntity.setCategory(categoryEntity);
        return SpendJson.fromEntity(spendEntity);
    }

    public List<SpendJson> findAllSpendingsByUsername(String username) {
        List<SpendEntity> spendEntities = spendDao.findAllByUsername(username);
        return spendEntities.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendDao.deleteSpend(spendEntity);
    }

}