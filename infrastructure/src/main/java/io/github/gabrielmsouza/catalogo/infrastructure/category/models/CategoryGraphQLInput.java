package io.github.gabrielmsouza.catalogo.infrastructure.category.models;

import io.github.gabrielmsouza.catalogo.domain.category.Category;

import java.time.Instant;

public record CategoryGraphQLInput(
        String id,
        String name,
        String description,
        Boolean active,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
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
