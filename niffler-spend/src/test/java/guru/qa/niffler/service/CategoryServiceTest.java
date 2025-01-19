package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = Assertions.assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + catName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

  @Test
  void getAllCategoriesShouldFilterArchived(@Mock CategoryRepository categoryRepository) {
    String username = "dasha";
    final CategoryEntity category = new CategoryEntity();
    category.setArchived(false);
    final CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setArchived(true);

    when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
            .thenReturn(List.of(category, archivedCategory));
    CategoryService categoryService = new CategoryService(categoryRepository);

    List<CategoryJson> result = categoryService.getAllCategories(username, true);
    assertEquals(result.size(), 1);
    assertFalse(result.getFirst().archived());
  }

  @Test
  void shouldNotUnarchiveCategoryWhenMaxCategoriesSizeExceeded(@Mock CategoryRepository categoryRepository) {
    String username = "dasha";
    final UUID id = UUID.randomUUID();
    final CategoryEntity category = new CategoryEntity();
    category.setArchived(true);
    category.setId(id);
    CategoryJson categoryJson = new CategoryJson(
            id,
            "Бары",
            username,
            false
    );

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(category));
    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    TooManyCategoriesException ex = Assertions.assertThrows(
            TooManyCategoriesException.class,
            () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
            "Can`t unarchive category for user: '" + username + "'",
            ex.getMessage()
    );
  }

  @Test
  void shouldSaveNewCategory(@Mock CategoryRepository categoryRepository) {
    String username = "dasha";
    CategoryJson categoryJson = new CategoryJson(
            null,
            "Рестораны",
            username,
            false
    );

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(1L);

    CategoryService categoryService = new CategoryService(categoryRepository);
    categoryService.save(categoryJson);

    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Рестораны", argumentCaptor.getValue().getName());
    assertEquals("dasha", argumentCaptor.getValue().getUsername());
    assertFalse(argumentCaptor.getValue().isArchived());
  }

  @Test
  void shouldNotSaveCategoryWhenMaxCategoriesSizeExceeded(@Mock CategoryRepository categoryRepository) {
    String username = "dasha";
    final UUID id = UUID.randomUUID();
    final CategoryEntity category = new CategoryEntity();
    category.setArchived(true);
    category.setId(id);
    CategoryJson categoryJson = new CategoryJson(
            id,
            "Бары",
            username,
            false
    );

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    TooManyCategoriesException ex = Assertions.assertThrows(
            TooManyCategoriesException.class,
            () -> categoryService.save(categoryJson)
    );
    Assertions.assertEquals(
            "Can`t add over than 8 categories for user: '" + username + "'",
            ex.getMessage()
    );
  }

  @Test
  void shouldNotSaveCategoryWithNameArchived(@Mock CategoryRepository categoryRepository) {
    String username = "dasha";
    String categoryName = "Archived";
    final UUID id = UUID.randomUUID();
    final CategoryEntity category = new CategoryEntity();
    category.setArchived(true);
    category.setId(id);
    CategoryJson categoryJson = new CategoryJson(
            id,
            categoryName,
            username,
            false
    );

    CategoryService categoryService = new CategoryService(categoryRepository);

    InvalidCategoryNameException ex = Assertions.assertThrows(
            InvalidCategoryNameException.class,
            () -> categoryService.save(categoryJson)
    );
    Assertions.assertEquals(
            "Can`t add category with name: '" + categoryName + "'",
            ex.getMessage()
    );
  }

}