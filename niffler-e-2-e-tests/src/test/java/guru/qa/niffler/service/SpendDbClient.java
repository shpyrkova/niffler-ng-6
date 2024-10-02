package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.spend.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.spend.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spend.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.spend.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDaoSpringJdbc = new CategoryDaoSpringJdbc();
    private final SpendDao spendDaoSpringJdbc = new SpendDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(
                    spendDao.create(spendEntity)
            );
        });
    }

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(CategoryJson category) {
        return CategoryJson.fromEntity(categoryDao
                .findByUsernameAndCategoryName(category.username(), category.name())
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
        categoryDao.delete(categoryEntity);
    }

    public SpendJson findSpendById(UUID id) {
        SpendEntity spendEntity = spendDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Spend not found"));
        // заполняем данные по категории именно здесь, так как в DAO мы должны работать только с одной таблицей
        CategoryEntity categoryEntity = categoryDao
                .findById(spendEntity.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        spendEntity.setCategory(categoryEntity);
        return SpendJson.fromEntity(spendEntity);
    }

    public List<SpendJson> findAllSpendingsByUsername(String username) {
        List<SpendEntity> spendEntities = spendDao.findAllByUsername(username);
        spendEntities.forEach(se -> se.setCategory(categoryDao
                .findById(se.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"))));
        return spendEntities.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendDao.delete(spendEntity);
    }

    public List<SpendJson> findAllSpendings() throws SQLException {
        List<SpendEntity> spendEntities = spendDao.findAll();
        return spendEntities.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public List<SpendJson> findAllSpendingsSpringJdbc() {
        List<SpendEntity> spendEntities = spendDaoSpringJdbc.findAll();
        return spendEntities.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public List<CategoryJson> findAllCategories() throws SQLException {
        List<CategoryEntity> categoryEntities = categoryDao.findAll();
        return categoryEntities.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

    public List<CategoryJson> findAllCategoriesSpringJdbc() {
        List<CategoryEntity> categoryEntities = categoryDaoSpringJdbc.findAll();
        return categoryEntities.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

}