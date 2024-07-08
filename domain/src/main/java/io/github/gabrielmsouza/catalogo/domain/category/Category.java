package io.github.gabrielmsouza.catalogo.domain.category;

import io.github.gabrielmsouza.catalogo.domain.validation.Error;
import io.github.gabrielmsouza.catalogo.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Objects;

public class Category {
    private String id;
    private final String name;
    private final String description;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant deletedAt;

    private Category(
        final String anId,
        final String aName,
        final String aDescription,
        final boolean isActive,
        final Instant aCreationDate,
        final Instant aUpdateDate,
        final Instant aDeleteDate
    ) {
        this.id = anId;
        this.name = aName;
        this.description = aDescription;
        this.active = isActive;
        this.createdAt = aCreationDate;
        this.updatedAt = aUpdateDate;
        this.deletedAt = aDeleteDate;
    }

    public static Category with(
        final String anId,
        final String aName,
        final String aDescription,
        final boolean isActive,
        final Instant aCreationDate,
        final Instant aUpdateDate,
        final Instant aDeleteDate
    ) {
        return new Category(anId, aName, aDescription, isActive, aCreationDate, aUpdateDate, aDeleteDate);
    }

    public static Category with(final Category aCategory) {
        return new Category(
            aCategory.id(),
            aCategory.name(),
            aCategory.description(),
            aCategory.active(),
            aCategory.createdAt(),
            aCategory.updatedAt(),
            aCategory.deletedAt()
        );
    }

    public Category validate(final ValidationHandler aHandler) {
        if (Objects.isNull(this.id) || this.id.isBlank()) {
            aHandler.append(Error.with("'id' should not be empty"));
        }

        if (Objects.isNull(this.name) || this.name.isBlank()) {
            aHandler.append(Error.with("'name' should not be empty"));
        }
        return this;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public boolean active() {
        return active;
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