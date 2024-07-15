package io.github.gabrielmsouza.catalogo.application.genre.save;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import io.github.gabrielmsouza.catalogo.domain.validation.Error;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class SaveGenreUseCase extends UseCase<SaveGenreUseCase.Input, SaveGenreUseCase.Output> {
    private final GenreGateway gateway;

    public SaveGenreUseCase(final GenreGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public Output execute(final Input input) {
        if (input == null) {
            throw DomainException.with(Error.with("'SaveGenreUseCase.Input' cannot be null"));
        }
        final var aGenre = Genre.with(
                input.id(),
                input.name(),
                input.active(),
                input.categories(),
                input.createdAt(),
                input.updatedAt(),
                input.deletedAt()
        );
        this.gateway.save(aGenre);
        return new Output(aGenre.id());
    }

    public record Input(
            String id,
            String name,
            boolean active,
            Set<String> categories,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {

    }

    public record Output(String id) {}
}
