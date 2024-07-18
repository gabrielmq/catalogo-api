package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.IntegrationTest;
import io.github.gabrielmsouza.catalogo.application.castmember.list.ListCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.save.SaveCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.infrastructure.WebGraphQlSecurityInterceptor;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
public class CastMemberGraphQLIT {
    @MockBean
    private ListCastMemberUseCase listCastMemberUseCase;

    @MockBean
    private SaveCastMemberUseCase saveCastMemberUseCase;

    @Autowired
    private WebGraphQlHandler webGraphQlHandler;

    @Autowired
    private WebGraphQlSecurityInterceptor interceptor;

    @Test
    public void givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
        interceptor.setAuthorities();
        final var document = "query castMembers { castMembers { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().expect(err -> "Unauthorized".equals(err.getMessage()) && "castMembers".equals(err.getPath()))
                .verify();
    }

    @Test
    public void givenUserWithAdminRole_whenQueries_shouldReturnResult() {
        interceptor.setAuthorities(Roles.ROLE_ADMIN);

        final var castMembers = List.of(
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.actor()),
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.director())
        );

        final var expectedIds = castMembers.stream().map(ListCastMemberUseCase.Output::id).toList();

        when(this.listCastMemberUseCase.execute(any()))
                .thenReturn(new Pagination<>(0, 10, castMembers.size(), castMembers));

        final var document = "query castMembers { castMembers { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().verify()
                .path("castMembers[*].id").entityList(String.class).isEqualTo(expectedIds);
    }

    @Test
    public void givenUserWithSubscriberRole_whenQueries_shouldReturnResult() {
        interceptor.setAuthorities(Roles.ROLE_SUBSCRIBER);

        final var castMembers = List.of(
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.actor()),
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.director())
        );

        final var expectedIds = castMembers.stream().map(ListCastMemberUseCase.Output::id).toList();

        when(this.listCastMemberUseCase.execute(any()))
                .thenReturn(new Pagination<>(0, 10, castMembers.size(), castMembers));

        final var document = "query castMembers { castMembers { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().verify()
                .path("castMembers[*].id").entityList(String.class).isEqualTo(expectedIds);
    }

    @Test
    public void givenUserWithCastMembersRole_whenQueries_shouldReturnResult() {
        interceptor.setAuthorities(Roles.ROLE_CAST_MEMBERS);

        final var castMembers = List.of(
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.actor()),
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.director())
        );

        final var expectedIds = castMembers.stream().map(ListCastMemberUseCase.Output::id).toList();

        when(this.listCastMemberUseCase.execute(any()))
                .thenReturn(new Pagination<>(0, 10, castMembers.size(), castMembers));

        final var document = "query castMembers { castMembers { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().verify()
                .path("castMembers[*].id").entityList(String.class).isEqualTo(expectedIds);
    }
}
