package io.github.gabrielmsouza.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenreEvent(@JsonProperty("id") String id) {
}
