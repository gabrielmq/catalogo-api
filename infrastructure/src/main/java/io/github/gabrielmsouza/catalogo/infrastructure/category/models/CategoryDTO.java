package io.github.gabrielmsouza.catalogo.infrastructure.category.models;

import io.github.gabrielmsouza.catalogo.domain.category.Category;

import java.time.Instant;

public record CategoryDTO(
        String id,
        String name,
        String description,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public Category toCategory() {
        return Category.with(
                this.id,
                this.name,
                this.description,
                this.isActive,
                this.createdAt,
                this.updatedAt,
                this.deletedAt
        );
    }

    @Override
    public Boolean isActive() {
        return isActive != null ? isActive : true;
    }
}
