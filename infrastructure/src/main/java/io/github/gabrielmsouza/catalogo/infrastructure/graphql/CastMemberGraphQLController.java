package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.application.castmember.list.ListCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.save.SaveCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.CastMemberGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberDTO;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.security.Roles;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

@Controller
public class CastMemberGraphQLController {
    private final ListCastMemberUseCase listCastMemberUseCase;
    private final SaveCastMemberUseCase saveCastMemberUseCase;

    public CastMemberGraphQLController(
            final ListCastMemberUseCase listCastMemberUseCase,
            final SaveCastMemberUseCase saveCastMemberUseCase
    ) {
        this.listCastMemberUseCase = Objects.requireNonNull(listCastMemberUseCase);
        this.saveCastMemberUseCase = Objects.requireNonNull(saveCastMemberUseCase);
    }

    @QueryMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_CAST_MEMBERS })
    public List<CastMemberGQL> castMembers(
            @Argument String search,
            @Argument int page,
            @Argument int perPage,
            @Argument String sort,
            @Argument String direction
    ) {
        final var aQuery = new CastMemberSearchQuery(page, perPage, search, sort, direction);
        return this.listCastMemberUseCase.execute(aQuery)
                .map(CastMemberGQLPresenter::present)
                .data();
    }

    @MutationMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_CAST_MEMBERS })
    public CastMemberGQL saveCastMember(@Argument CastMemberDTO input) {
        final var aCastMember = input.toCastMember();
        return CastMemberGQLPresenter.present(this.saveCastMemberUseCase.execute(aCastMember));
    }
}

