package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.Nonnull;

public interface SpendClient {

    @Nonnull
    SpendJson createSpend(SpendJson spend);

    @Nonnull
    CategoryJson createCategory(CategoryJson category);

    @Nonnull
    CategoryJson updateCategory(CategoryJson category);

    CategoryJson findCategoryByUsernameAndCategoryName(CategoryJson category);

    void removeCategory(CategoryJson category);

}