package io.github.gabrielmsouza.catalogo.application.castmember.list;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase.Output;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
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

public class ListCastMemberUseCaseTest extends UseCaseTest {
    @InjectMocks
    private ListCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway gateway;

    @Test
    void givenValidQuery_whenCallsListCastMember_thenShouldReturnCastMember() {
        // given
        final var members = List.of(
                Fixture.CastMembers.director(),
                Fixture.CastMembers.actor()
        );

        final var expectedItems = members.stream()
                .map(ListCastMemberUseCase.Output::from)
                .toList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "abc";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 2;

        final var aQuery = new CastMemberSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                members.size(),
                members
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
