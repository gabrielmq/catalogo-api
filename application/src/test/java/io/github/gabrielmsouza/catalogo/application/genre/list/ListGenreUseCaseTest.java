package io.github.gabrielmsouza.catalogo.application.genre.list;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ListGenreUseCaseTest extends UseCaseTest {
    @InjectMocks
    private ListGenreUseCase useCase;

    @Mock
    private GenreGateway gateway;

    @Test
    void givenValidQuery_whenCallsListGenres_thenShouldReturnCategories() {
        // given
        final var genres = List.of(
                Fixture.Genres.business(),
                Fixture.Genres.tech()
        );

        final var expectedItems = genres.stream()
                .map(ListGenreUseCase.Output::from)
                .toList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "aulas";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedCategories = Set.of("c1");

        final var expectedItemsCount = 2;

        final var input = new ListGenreUseCase.Input(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                expectedCategories
        );

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                genres.size(),
                genres
        );

        when(gateway.findAll(any())).thenReturn(pagination);

        // when
        final var actualOutput = useCase.execute(input);

        // then
        assertEquals(expectedPage, actualOutput.meta().currentPage());
        assertEquals(expectedPerPage, actualOutput.meta().perPage());
        assertEquals(expectedItemsCount, actualOutput.meta().total());
        assertTrue(
                expectedItems.size() == actualOutput.data().size()
                        && expectedItems.containsAll(actualOutput.data())
        );
    }
}
