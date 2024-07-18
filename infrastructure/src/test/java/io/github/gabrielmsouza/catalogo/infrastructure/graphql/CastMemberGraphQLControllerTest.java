package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.GraphQLControllerTest;
import io.github.gabrielmsouza.catalogo.application.castmember.list.ListCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.save.SaveCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberType;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.CastMemberGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberGQL;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQLControllerTest(controllers = CastMemberGraphQLController.class)
public class CastMemberGraphQLControllerTest {
    @MockBean
    private ListCastMemberUseCase listCastMemberUseCase;

    @MockBean
    private SaveCastMemberUseCase saveCastMemberUseCase;

    @Autowired
    private GraphQlTester graphql;

    @Test
    void givenDefaultArguments_whenCallsListCastMembers_thenShouldReturn() {
        // given
        final var castMembers  = List.of(
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.actor()),
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.director())
        );

        final var expectedMembers = castMembers.stream().map(CastMemberGQLPresenter::present).toList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedSearch = "";

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                castMembers.size(),
                castMembers
        );

        when(this.listCastMemberUseCase.execute(any())).thenReturn(pagination);

        final var query = """
                    {
                        castMembers {
                            id
                            name
                            type
                            createdAt
                            updatedAt
                        }
                    }
                """;

        // when
        final var res = this.graphql.document(query).execute();
        final var actualCastMember = res.path("castMembers")
                .entityList(CastMemberGQL.class)
                .get();

        // then
        assertTrue(
                actualCastMember.size() == expectedMembers.size()
                        && actualCastMember.containsAll(expectedMembers)
        );

        final var captor = ArgumentCaptor.forClass(CastMemberSearchQuery.class);
        verify(this.listCastMemberUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSearch, actualQuery.terms());
    }

    @Test
    void givenCustomArguments_whenCallsListCastMembers_thenShouldReturn() {
        // given
        final var castMembers  = List.of(
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.actor()),
                ListCastMemberUseCase.Output.from(Fixture.CastMembers.director())
        );

        final var expectedMembers = castMembers.stream().map(CastMemberGQLPresenter::present).toList();

        final var expectedPage = 2;
        final var expectedPerPage = 15;
        final var expectedSort = "id";
        final var expectedDirection = "desc";
        final var expectedSearch = "asd";

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                castMembers.size(),
                castMembers
        );

        when(this.listCastMemberUseCase.execute(any())).thenReturn(pagination);

        final var query = """
                    query AllCastMembers($search: String, $page: Int, $perPage: Int, $sort: String, $direction: String) {
                        castMembers(search: $search, page: $page, perPage: $perPage, sort: $sort, direction: $direction) {
                            id
                            name
                            type
                            createdAt
                            updatedAt
                        }
                    }
                """;

        // when
        final var res = this.graphql.document(query)
                .variable("search", expectedSearch)
                .variable("page", expectedPage)
                .variable("perPage", expectedPerPage)
                .variable("sort", expectedSort)
                .variable("direction", expectedDirection)
                .execute();

        final var actualCastMember = res.path("castMembers")
                .entityList(CastMemberGQL.class)
                .get();

        // then
        assertTrue(
                actualCastMember.size() == expectedMembers.size()
                        && actualCastMember.containsAll(expectedMembers)
        );

        final var captor = ArgumentCaptor.forClass(CastMemberSearchQuery.class);
        verify(this.listCastMemberUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSearch, actualQuery.terms());
    }

    @Test
    public void givenCategoryInput_whenCallsSaveCastMemberMutation_thenShouldPersistAndReturn() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "John Doe";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedCreatedAt = InstantUtils.now();
        final var expectedUpdatedAt = InstantUtils.now();

        final var input = Map.of(
                "id", expectedId,
                "name", expectedName,
                "type", expectedType.name(),
                "createdAt", expectedCreatedAt.toString(),
                "updatedAt", expectedUpdatedAt.toString()
        );

        final var query = """
                mutation SaveCastMember($input: CastMemberInput!) {
                    castMember: saveCastMember(input: $input) {
                        id
                        name
                        type
                        createdAt
                        updatedAt
                    }
                }
                """;

        doAnswer(returnsFirstArg()).when(this.saveCastMemberUseCase).execute(any());

        // when
        this.graphql.document(query)
                .variable("input", input)
                .execute()
                .path("castMember.id").entity(String.class).isEqualTo(expectedId)
                .path("castMember.name").entity(String.class).isEqualTo(expectedName)
                .path("castMember.type").entity(String.class).isEqualTo(expectedType.name())
                .path("castMember.createdAt").entity(String.class).isEqualTo(expectedCreatedAt.toString())
                .path("castMember.updatedAt").entity(String.class).isEqualTo(expectedUpdatedAt.toString());

        // then
        final var captor = ArgumentCaptor.forClass(CastMember.class);
        verify(this.saveCastMemberUseCase).execute(captor.capture());

        final var actualCastMember = captor.getValue();
        assertEquals(expectedId, actualCastMember.id());
        assertEquals(expectedName, actualCastMember.name());
        assertEquals(expectedType, actualCastMember.type());
        assertEquals(expectedCreatedAt, actualCastMember.createdAt());
        assertEquals(expectedUpdatedAt, actualCastMember.updatedAt());
    }
}
