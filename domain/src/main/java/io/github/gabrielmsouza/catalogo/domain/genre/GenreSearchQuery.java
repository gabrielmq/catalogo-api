package io.github.gabrielmsouza.catalogo.domain.genre;

public record GenreSearchQuery(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {
}
