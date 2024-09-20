package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CreateCategoryExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateCategoryExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final String categoryName = randomCategoryName();

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(annotation -> {
                    CategoryJson category = new CategoryJson(
                            null,
                            annotation.name().isEmpty() ? categoryName : annotation.name(),
                            annotation.username(),
                            false
                    );
                    CategoryJson createdCategory = spendApiClient.createCategory(category);
                    if (annotation.archived()) {
                        CategoryJson archivedCategory = new CategoryJson(
                                createdCategory.id(),
                                createdCategory.name(),
                                createdCategory.username(),
                                true
                        );
                        createdCategory = spendApiClient.updateCategory(archivedCategory);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);

                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(CreateCategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
            spendApiClient.updateCategory(new CategoryJson(
                    category.id(),
                    category.name(),
                    category.username(),
                    true
            ));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CreateCategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

}
