package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreDTO;

import java.util.Optional;

public interface GenreClient {
    Optional<GenreDTO> genreOfId(String genreId);
}
