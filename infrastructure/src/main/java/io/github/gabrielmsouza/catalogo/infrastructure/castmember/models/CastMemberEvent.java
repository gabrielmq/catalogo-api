package io.github.gabrielmsouza.catalogo.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberType;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;

public record CastMemberEvent(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("created_at") Long createdAt,
        @JsonProperty("updated_at") Long updatedAt
) {
    public static CastMemberEvent from(final CastMember aMember) {
        return new CastMemberEvent(
                aMember.id(),
                aMember.name(),
                aMember.type().name(),
                aMember.createdAt().toEpochMilli(),
                aMember.updatedAt().toEpochMilli()
        );
    }

    public CastMember toCastMember() {
        return CastMember.with(id, name, CastMemberType.of(type), InstantUtils.fromTimestamp(createdAt), InstantUtils.fromTimestamp(updatedAt));
    }
}
