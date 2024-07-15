package io.github.gabrielmsouza.catalogo.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberType;

import java.time.Instant;

public record CastMemberDTO(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
    public CastMember toCastMember() {
        return CastMember.with(id, name, CastMemberType.of(type), createdAt, updatedAt);
    }
}
