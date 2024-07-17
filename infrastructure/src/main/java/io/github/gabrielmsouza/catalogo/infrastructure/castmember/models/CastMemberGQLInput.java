package io.github.gabrielmsouza.catalogo.infrastructure.castmember.models;

import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberType;

import java.time.Instant;

public record CastMemberGQLInput(
        String id,
        String name,
        String type,
        Instant createdAt,
        Instant updatedAt
) {
    public CastMember toCastMember() {
        return CastMember.with(id, name, CastMemberType.of(type), createdAt, updatedAt);
    }
}
