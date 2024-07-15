package io.github.gabrielmsouza.catalogo.domain.genre;

import io.github.gabrielmsouza.catalogo.domain.validation.Error;
import io.github.gabrielmsouza.catalogo.domain.validation.ValidationHandler;
import io.github.gabrielmsouza.catalogo.domain.validation.handler.ThrowsValidationHandler;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Genre {
    private String id;
    private String name;
    private boolean active;
    private Set<String> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(
            final String id,
            final String name,
            final boolean active,
            final Set<String> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.categories = categories != null ? categories : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        validate(new ThrowsValidationHandler());
    }

    public Genre validate(final ValidationHandler handler) {
        if (Objects.isNull(this.id) || this.id.isBlank()) {
            handler.append(Error.with("'id' should not be empty"));
        }
        if (Objects.isNull(this.name) || this.name.isBlank()) {
            handler.append(Error.with("'name' should not be empty"));
        }
        return this;
    }

    public static Genre with(
            final String id,
            final String name,
            final boolean active,
            final Set<String> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Genre(id, name, active, categories, createdAt, updatedAt, deletedAt);
    }

    public static Genre with(final Genre aGenre) {
        return with(
                aGenre.id(),
                aGenre.name(),
                aGenre.active(),
                aGenre.categories(),
                aGenre.createdAt(),
                aGenre.updatedAt(),
                aGenre.deletedAt()
        );
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public boolean active() {
        return active;
    }

    public Set<String> categories() {
        return categories;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }
}
