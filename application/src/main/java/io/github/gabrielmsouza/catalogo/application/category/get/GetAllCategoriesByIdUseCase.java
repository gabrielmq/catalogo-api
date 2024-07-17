package io.github.gabrielmsouza.catalogo.application.category.get;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GetAllCategoriesByIdUseCase extends UseCase<GetAllCategoriesByIdUseCase.Input, List<GetAllCategoriesByIdUseCase.Output>> {
    private final CategoryGateway categoryGateway;

    public GetAllCategoriesByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public List<Output> execute(final Input in) {
        if (in.ids().isEmpty()) {
            return List.of();
        }

        return this.categoryGateway.findAllById(in.ids()).stream()
                .map(Output::new)
                .toList();
    }

    public record Input(Set<String> ids) {
        @Override
        public Set<String> ids() {
            return ids != null ? ids : Collections.emptySet();
        }
    }

    public record Output(
            String id,
            String name,
            String description
    ) {

        public Output(final Category aCategory) {
            this(
                    aCategory.id(),
                    aCategory.name(),
                    aCategory.description()
            );
        }
    }
}
