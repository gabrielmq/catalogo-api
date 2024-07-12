package io.github.gabrielmsouza.catalogo.domain.castmember;

import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface CastMemberGateway {
    CastMember save(CastMember aMember);
    void deleteById(String anId);
    Optional<CastMember> findById(String anId);
    Pagination<CastMember> findAll(CastMemberSearchQuery aQuery);
}
