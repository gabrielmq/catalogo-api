package io.github.gabrielmsouza.catalogo.infrastructure.genre.models;

import io.github.gabrielmsouza.catalogo.domain.genre.Genre;

import java.time.Instant;
import java.util.Set;

public record GenreGraphQLInput(
        String id,
        String name,
        Boolean active,
        Set<String> categories,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public static GenreGraphQLInput from(final Genre genre) {
        return new GenreGraphQLInput(
                genre.id(),
                genre.name(),
                genre.active(),
                genre.categories(),
                genre.createdAt(),
                genre.updatedAt(),
                genre.deletedAt()
        );
    }
}
