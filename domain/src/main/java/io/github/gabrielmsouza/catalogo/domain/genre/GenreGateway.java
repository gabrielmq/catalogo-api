package io.github.gabrielmsouza.catalogo.domain.genre;

import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface GenreGateway {
    Genre save(Genre aGenre);
    void deleteById(String genreId);
    Optional<Genre> findById(String genreId);
    Pagination<Genre> findAll(GenreSearchQuery aQuery);
}
