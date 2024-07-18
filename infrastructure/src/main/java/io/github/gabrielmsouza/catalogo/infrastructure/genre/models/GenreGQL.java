package io.github.gabrielmsouza.catalogo.infrastructure.genre.models;

import java.util.Set;

public record GenreGQL(
        String id,
        String name,
        Boolean active,
        Set<String> categories,
        String createdAt,
        String updatedAt,
        String deletedAt
) {
}
