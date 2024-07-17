package io.github.gabrielmsouza.catalogo.infrastructure.castmember.models;

public record CastMemberGQL(
        String id,
        String name,
        String type,
        String createdAt,
        String updatedAt
) {
}
