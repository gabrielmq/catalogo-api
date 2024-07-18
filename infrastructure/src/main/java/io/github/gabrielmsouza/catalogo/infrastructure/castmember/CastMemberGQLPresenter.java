package io.github.gabrielmsouza.catalogo.infrastructure.castmember;

import io.github.gabrielmsouza.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.list.ListCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberGQL;

public final class CastMemberGQLPresenter {
    private CastMemberGQLPresenter() {}

    public static CastMemberGQL present(final ListCastMemberUseCase.Output out) {
        return new CastMemberGQL(
                out.id(),
                out.name(),
                out.type(),
                out.createdAt().toString(),
                out.updatedAt().toString()
        );
    }

    public static CastMemberGQL present(final GetAllCastMembersByIdUseCase.Output out) {
        return new CastMemberGQL(
                out.id(),
                out.name(),
                out.type().name(),
                out.createdAt().toString(),
                out.updatedAt().toString()
        );
    }

    public static CastMemberGQL present(final CastMember out) {
        return new CastMemberGQL(
                out.id(),
                out.name(),
                out.type().name(),
                out.createdAt().toString(),
                out.updatedAt().toString()
        );
    }
}
