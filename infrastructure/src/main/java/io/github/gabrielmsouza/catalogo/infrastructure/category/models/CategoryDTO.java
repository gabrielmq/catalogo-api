package io.github.gabrielmsouza.catalogo.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gabrielmsouza.catalogo.domain.category.Category;

import java.time.Instant;

public record CategoryDTO(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {

    public Category toCategory() {
        return Category.with(
                this.id,
                this.name,
                this.description,
                this.active,
                this.createdAt,
                this.updatedAt,
                this.deletedAt
        );
    }

    @Override
    public Boolean active() {
        return active != null ? active : true;
    }
}
