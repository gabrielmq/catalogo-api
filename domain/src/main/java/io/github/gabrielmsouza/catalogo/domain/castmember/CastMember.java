package io.github.gabrielmsouza.catalogo.domain.castmember;

import io.github.gabrielmsouza.catalogo.domain.validation.Error;
import io.github.gabrielmsouza.catalogo.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Objects;

public class CastMember {
    private final String id;
    private final String name;
    private final CastMemberType type;
    private final Instant createdAt;
    private final Instant updatedAt;

    private CastMember(
            final String id,
            final String name,
            final CastMemberType type,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CastMember with(
            final String id,
            final String name,
            final CastMemberType type,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new CastMember(id, name, type, createdAt, updatedAt);
    }

    public static CastMember with(final CastMember castMember) {
        return new CastMember(
                castMember.id,
                castMember.name,
                castMember.type,
                castMember.createdAt,
                castMember.updatedAt
        );
    }

    public CastMember validate(final ValidationHandler handler) {
        if (Objects.isNull(this.id) || this.id.isBlank()) {
            handler.append(Error.with("'id' should not be empty"));
        }
        if (Objects.isNull(this.name) || this.name.isBlank()) {
            handler.append(Error.with("'name' should not be empty"));
        }
        if (Objects.isNull(this.type)) {
            handler.append(Error.with("'type' should not be null"));
        }
        return this;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public CastMemberType type() {
        return type;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
