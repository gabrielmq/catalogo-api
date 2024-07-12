package io.github.gabrielmsouza.catalogo.application.category.list;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase.Output;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ListCategoryUseCaseTest extends UseCaseTest {
    @InjectMocks
    private ListCategoryUseCase useCase;

    @Mock
    private CategoryGateway gateway;

    @Test
    void givenValidQuery_whenCallsListCategories_thenShouldReturnCategories() {
        // given
        final var categories = List.of(
                Fixture.Categories.aulas(),
                Fixture.Categories.lives()
        );

        final var expectedItems = categories.stream()
                .map(Output::from)
                .toList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "aulas";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 2;

        final var aQuery = new CategorySearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                categories.size(),
                categories
        );

        when(gateway.findAll(any())).thenReturn(pagination);

        // when
        final var actualOutput = useCase.execute(aQuery);

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
