package io.github.gabrielmsouza.catalogo.application.category.delete;

import io.github.gabrielmsouza.catalogo.application.UnitUseCase;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;

import java.util.Objects;

public class DeleteCategoryUseCase extends UnitUseCase<String> {
    private final CategoryGateway gateway;

    public DeleteCategoryUseCase(final CategoryGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public void execute(final String anIn) {
        if (Objects.nonNull(anIn)) {
            this.gateway.deleteById(anIn);
        }
    }
}
