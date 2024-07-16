package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryGraphQLInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

@Controller
public class CategoryGraphQLController {
    private final ListCategoryUseCase listCategoryUseCase;
    private final SaveCategoryUseCase saveCategoryUseCase;

    public CategoryGraphQLController(
            final ListCategoryUseCase listCategoryUseCase,
            final SaveCategoryUseCase saveCategoryUseCase
    ) {
        this.listCategoryUseCase = Objects.requireNonNull(listCategoryUseCase);
        this.saveCategoryUseCase = Objects.requireNonNull(saveCategoryUseCase);
    }

    @QueryMapping
    public List<ListCategoryUseCase.Output> categories(
            @Argument String search,
            @Argument int page,
            @Argument int perPage,
            @Argument String sort,
            @Argument String direction
    ) {
        final var aQuery = new CategorySearchQuery(page, perPage, search, sort, direction);
        return this.listCategoryUseCase.execute(aQuery).data();
    }

    @MutationMapping
    public Category saveCategory(@Argument CategoryGraphQLInput input) {
        final var aCategory = input.toCategory();
        this.saveCategoryUseCase.execute(aCategory);
        return aCategory;
    }
}

