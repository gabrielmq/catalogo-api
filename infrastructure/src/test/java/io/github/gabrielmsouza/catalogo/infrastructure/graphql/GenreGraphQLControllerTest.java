package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.GraphQLControllerTest;
import io.github.gabrielmsouza.catalogo.application.genre.list.ListGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.save.SaveGenreUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.GenreGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreGQL;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQLControllerTest(controllers = GenreGraphQLController.class)
public class GenreGraphQLControllerTest {
    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @MockBean
    private SaveGenreUseCase saveGenreUseCase;

    @Autowired
    private GraphQlTester graphql;

    @Test
    void givenDefaultArguments_whenCallsListGenres_thenShouldReturn() {
        // given
        final var genres = List.of(
                ListGenreUseCase.Output.from(Fixture.Genres.business()),
                ListGenreUseCase.Output.from(Fixture.Genres.tech())
        );

        final var expectedGenres = genres.stream().map(GenreGQLPresenter::present).toList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedSearch = "";
        final var expectedCategories = Set.of();

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                genres.size(),
                genres
        );

        when(this.listGenreUseCase.execute(any())).thenReturn(pagination);

        final var query = """
                    {
                        genres {
                            id
                            name
                            active
                            categories
                            createdAt
                            updatedAt
                            deletedAt
                        }
                    }
                """;

        // when
        final var res = this.graphql.document(query).execute();
        final var actualGenres = res.path("genres")
                .entityList(GenreGQL.class)
                .get();

        // then
        assertTrue(
                actualGenres.size() == expectedGenres.size()
                        && actualGenres.containsAll(expectedGenres)
        );

        final var captor = ArgumentCaptor.forClass(ListGenreUseCase.Input.class);
        verify(this.listGenreUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSearch, actualQuery.terms());
        assertEquals(expectedCategories, actualQuery.categories());
    }

    @Test
    void givenCustomArguments_whenCallsListGenres_thenShouldReturn() {
        // given
        final var genres = List.of(
                ListGenreUseCase.Output.from(Fixture.Genres.business()),
                ListGenreUseCase.Output.from(Fixture.Genres.tech())
        );

        final var expectedGenres = genres.stream().map(GenreGQLPresenter::present).toList();

        final var expectedPage = 2;
        final var expectedPerPage = 15;
        final var expectedSort = "id";
        final var expectedDirection = "desc";
        final var expectedSearch = "asd";
        final var expectedCategories = Set.of("c1");

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                genres.size(),
                genres
        );

        when(this.listGenreUseCase.execute(any())).thenReturn(pagination);

        final var query = """
                    query AllGenres($search: String, $page: Int, $perPage: Int, $sort: String, $direction: String, $categories: [String]) {
                        genres(search: $search, page: $page, perPage: $perPage, sort: $sort, direction: $direction, categories: $categories) {
                            id
                            name
                            active
                            categories
                            createdAt
                            updatedAt
                            deletedAt
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
                .variable("categories", expectedCategories)
                .execute();

        final var actualGenres = res.path("genres")
                .entityList(GenreGQL.class)
                .get();

        // then
        assertTrue(
                actualGenres.size() == expectedGenres.size()
                        && actualGenres.containsAll(expectedGenres)
        );

        final var captor = ArgumentCaptor.forClass(ListGenreUseCase.Input.class);
        verify(this.listGenreUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSearch, actualQuery.terms());
        assertEquals(expectedCategories, actualQuery.categories());
    }

    @Test
    public void givenInactiveGenreInput_whenCallsSaveGenreMutation_shouldPersistAndReturn() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Business";
        final var expectedIsActive = false;
        final var expectedCategories = Set.of("c1", "c2");
        final var expectedDates = InstantUtils.now();

        final var input = Map.of(
                "id", expectedId,
                "name", expectedName,
                "active", expectedIsActive,
                "categories", expectedCategories,
                "createdAt", expectedDates.toString(),
                "updatedAt", expectedDates.toString(),
                "deletedAt", expectedDates.toString()
        );

        final var query = """
                mutation SaveGenre($input: GenreInput!) {
                    genre: saveGenre(input: $input) {
                        id
                    }
                }
                """;

        doReturn(new SaveGenreUseCase.Output(expectedId)).when(this.saveGenreUseCase).execute(any());

        // when
        this.graphql.document(query)
                .variable("input", input)
                .execute()
                .path("genre.id").entity(String.class).isEqualTo(expectedId);

        // then
        final var captor = ArgumentCaptor.forClass(SaveGenreUseCase.Input.class);

        verify(this.saveGenreUseCase, times(1)).execute(captor.capture());

        final var actualGenre = captor.getValue();
        assertEquals(expectedId, actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.active());
        assertEquals(expectedCategories, actualGenre.categories());
        assertEquals(expectedDates, actualGenre.createdAt());
        assertEquals(expectedDates, actualGenre.updatedAt());
        assertEquals(expectedDates, actualGenre.deletedAt());
    }

    @Test
    public void givenActiveGenreInputWithoutDeletedAt_whenCallsSaveGenreMutation_shouldPersistAndReturn() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("c1", "c2");
        final var expectedDates = InstantUtils.now();

        final var input = Map.of(
                "id", expectedId,
                "name", expectedName,
                "active", expectedIsActive,
                "categories", expectedCategories,
                "createdAt", expectedDates.toString(),
                "updatedAt", expectedDates.toString()
        );

        final var query = """
                mutation SaveGenre($input: GenreInput!) {
                    genre: saveGenre(input: $input) {
                        id
                    }
                }
                """;

        doReturn(new SaveGenreUseCase.Output(expectedId)).when(this.saveGenreUseCase).execute(any());

        // when
        this.graphql.document(query)
                .variable("input", input)
                .execute()
                .path("genre.id").entity(String.class).isEqualTo(expectedId);

        // then
        final var captor = ArgumentCaptor.forClass(SaveGenreUseCase.Input.class);

        verify(this.saveGenreUseCase, times(1)).execute(captor.capture());

        final var actualGenre = captor.getValue();
        assertEquals(expectedId, actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.active());
        assertEquals(expectedCategories, actualGenre.categories());
        assertEquals(expectedDates, actualGenre.createdAt());
        assertEquals(expectedDates, actualGenre.updatedAt());
        assertNull(actualGenre.deletedAt());
    }
}
