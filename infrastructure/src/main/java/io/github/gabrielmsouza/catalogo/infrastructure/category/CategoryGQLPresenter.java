package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.application.category.get.GetAllCategoriesByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryGQL;

public final class CategoryGQLPresenter {
    private CategoryGQLPresenter() {}

    public static CategoryGQL present(final ListCategoryUseCase.Output out) {
        return new CategoryGQL(out.id(), out.name(), out.description());
    }

    public static CategoryGQL present(final GetAllCategoriesByIdUseCase.Output out) {
        return new CategoryGQL(out.id(), out.name(), out.description());
    }

    public static CategoryGQL present(final Category out) {
        return new CategoryGQL(out.id(), out.name(), out.description());
    }
}
