package io.github.gabrielmsouza.catalogo.application.category.list;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.util.Objects;

public class ListCategoryUseCase extends UseCase<CategorySearchQuery, Pagination<ListCategoryUseCase.Output>> {
    private final CategoryGateway gateway;

    public ListCategoryUseCase(final CategoryGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    public Pagination<Output> execute(final CategorySearchQuery aQuery) {
        return this.gateway.findAll(aQuery).map(Output::from);
    }

    public record Output(
            String id,
            String name
    ) {
        public static Output from(final Category aCategory) {
            return new Output(aCategory.id(), aCategory.name());
        }
    }
}
