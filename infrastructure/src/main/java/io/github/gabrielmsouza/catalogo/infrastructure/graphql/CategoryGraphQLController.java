package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.infrastructure.category.CategoryGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryGQLInput;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.security.Roles;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
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
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_CATEGORIES })
    public List<CategoryGQL> categories(
            @Argument String search,
            @Argument int page,
            @Argument int perPage,
            @Argument String sort,
            @Argument String direction
    ) {
        final var aQuery = new CategorySearchQuery(page, perPage, search, sort, direction);
        return this.listCategoryUseCase.execute(aQuery)
                .map(CategoryGQLPresenter::present)
                .data();
    }

    @MutationMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_CATEGORIES })
    public CategoryGQL saveCategory(@Argument CategoryGQLInput input) {
        return CategoryGQLPresenter.present(this.saveCategoryUseCase.execute(input.toCategory()));
    }
}

