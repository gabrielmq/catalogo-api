package io.github.gabrielmsouza.catalogo.domain.castmember;

public record CastMemberSearchQuery(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {
}
