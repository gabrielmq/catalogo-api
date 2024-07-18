package io.github.gabrielmsouza.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoEvent(@JsonProperty("id") String id) {
}
