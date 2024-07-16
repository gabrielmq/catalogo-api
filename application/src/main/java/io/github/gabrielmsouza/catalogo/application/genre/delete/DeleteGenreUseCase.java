package io.github.gabrielmsouza.catalogo.application.genre.delete;

import io.github.gabrielmsouza.catalogo.application.UnitUseCase;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;

import java.util.Objects;

public class DeleteGenreUseCase extends UnitUseCase<String> {
    private final GenreGateway gateway;

    public DeleteGenreUseCase(final GenreGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(final String anId) {
        if (Objects.nonNull(anId)) {
            this.gateway.deleteById(anId);
        }
    }
}
