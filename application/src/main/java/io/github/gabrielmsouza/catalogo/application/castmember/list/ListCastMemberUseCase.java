package io.github.gabrielmsouza.catalogo.application.castmember.list;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.time.Instant;

public class ListCastMemberUseCase extends UseCase<CastMemberSearchQuery, Pagination<ListCastMemberUseCase.Output>> {
    private final CastMemberGateway gateway;

    public ListCastMemberUseCase(final CastMemberGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Pagination<Output> execute(final CastMemberSearchQuery aQuery) {
        return this.gateway.findAll(aQuery).map(Output::from);
    }

    public record Output(
            String id,
            String name,
            String type,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static Output from(final CastMember aCastMember) {
            return new Output(
                    aCastMember.id(),
                    aCastMember.name(),
                    aCastMember.type().name(),
                    aCastMember.createdAt(),
                    aCastMember.updatedAt()
            );
        }
    }
}
