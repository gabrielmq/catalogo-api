package io.github.gabrielmsouza.catalogo.infrastructure.castmember;

import io.github.gabrielmsouza.catalogo.AbstractElasticsearchTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.persistence.CastMemberDocument;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryDocument;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CastMemberElasticsearchGatewayTest extends AbstractElasticsearchTest {
    @Autowired
    private CastMemberElasticsearchGateway gateway;

    @Autowired
    private CastMemberRepository repository;

    @Test
    void testInjection() {
        assertNotNull(gateway);
        assertNotNull(repository);
    }

    @Test
    void givenValidCastMember_whenCallsSave_thenShouldPersistIt() {
        // given
        final var director = Fixture.CastMembers.director();

        // when
        final var actualOutput = this.gateway.save(director);

        // then
        assertEquals(director, actualOutput);

        final var actualMember = this.repository.findById(director.id()).get();
        assertEquals(director.id(), actualMember.id());
        assertEquals(director.name(), actualMember.name());
        assertEquals(director.type(), actualMember.type());
        assertEquals(director.createdAt(), actualMember.createdAt());
        assertEquals(director.updatedAt(), actualMember.updatedAt());
    }

    @Test
    void givenValidId_whenCallsDeleteById_thenShouldDeleteIt() {
        // given
        final var director = Fixture.CastMembers.director();

        this.repository.save(CastMemberDocument.from(director));

        final var expectedId = director.id();

        assertTrue(this.repository.existsById(expectedId));

        // when
        this.gateway.deleteById(expectedId);

        // then
        assertFalse(this.repository.existsById(expectedId));
    }

    @Test
    void givenInvalidId_whenCallsDeleteById_thenShouldBeOk() {
        // given
        final var expectedId = "any";

        // when/then
        assertDoesNotThrow(() -> this.gateway.deleteById(expectedId));
    }

    @Test
    void givenValidId_whenCallsFindById_thenShouldRetrieveIt() {
        // given
        final var actor = Fixture.CastMembers.actor();

        this.repository.save(CastMemberDocument.from(actor));

        final var expectedId = actor.id();

        assertTrue(this.repository.existsById(expectedId));

        // when
        final var actualOutput = this.gateway.findById(expectedId).get();

        // then
        assertEquals(actor.id(), actualOutput.id());
        assertEquals(actor.name(), actualOutput.name());
        assertEquals(actor.type(), actualOutput.type());
        assertEquals(actor.createdAt(), actualOutput.createdAt());
        assertEquals(actor.updatedAt(), actualOutput.updatedAt());
    }

    @Test
    void givenInvalidId_whenCallsFindById_thenShouldReturnEmpty() {
        // given
        final var expectedId = "any";

        // when
        final var actualOutput = this.gateway.findById(expectedId);

        // then
        assertTrue(actualOutput.isEmpty());
    }

    @Test
    public void givenEmptyCastMembers_whenCallsFindAll_thenShouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new CastMemberSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        // when
        final var actualOutput = this.gateway.findAll(aQuery);

        // then
        assertEquals(expectedPage, actualOutput.meta().currentPage());
        assertEquals(expectedPerPage, actualOutput.meta().perPage());
        assertEquals(expectedTotal, actualOutput.meta().total());
        assertEquals(expectedTotal, actualOutput.data().size());
    }

    @ParameterizedTest
    @CsvSource({
            "joh,0,10,1,1,John",
            "mar,0,10,1,1,Mary"
    })
    public void givenValidTerm_whenCallsFindAll_thenShouldReturnElementsFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockCategories();

        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CastMemberSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        // when
        final var actualOutput = this.gateway.findAll(aQuery);

        // then
        assertEquals(expectedPage, actualOutput.meta().currentPage());
        assertEquals(expectedPerPage, actualOutput.meta().perPage());
        assertEquals(expectedTotal, actualOutput.meta().total());
        assertEquals(expectedItemsCount, actualOutput.data().size());
        assertEquals(expectedName, actualOutput.data().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,3,3,John",
            "name,desc,0,10,3,3,Unknown",
            "created_at,asc,0,10,3,3,John",
            "created_at,desc,0,10,3,3,Unknown",
    })
    public void givenValidSortAndDirection_whenCallsFindAll_thenShouldReturnElementsSorted(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockCategories();

        final var expectedTerms = "";

        final var aQuery = new CastMemberSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        // when
        final var actualOutput = this.gateway.findAll(aQuery);

        // then
        assertEquals(expectedPage, actualOutput.meta().currentPage());
        assertEquals(expectedPerPage, actualOutput.meta().perPage());
        assertEquals(expectedTotal, actualOutput.meta().total());
        assertEquals(expectedItemsCount, actualOutput.data().size());
        assertEquals(expectedName, actualOutput.data().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,1,3,John",
            "1,1,1,3,Mary",
            "2,1,1,3,Unknown",
            "3,1,0,3,",
    })
    public void givenValidPage_whenCallsFindAll_thenShouldReturnElementsPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockCategories();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CastMemberSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        // when
        final var actualOutput = this.gateway.findAll(aQuery);

        // then
        assertEquals(expectedPage, actualOutput.meta().currentPage());
        assertEquals(expectedPerPage, actualOutput.meta().perPage());
        assertEquals(expectedTotal, actualOutput.meta().total());
        assertEquals(expectedItemsCount, actualOutput.data().size());

        if (StringUtils.isNotEmpty(expectedName)) {
            assertEquals(expectedName, actualOutput.data().get(0).name());
        }
    }

    private void mockCategories() {
        this.repository.save(CastMemberDocument.from(Fixture.CastMembers.director("John")));
        this.repository.save(CastMemberDocument.from(Fixture.CastMembers.actor("Mary")));
        this.repository.save(CastMemberDocument.from(Fixture.CastMembers.unknown("Unknown")));
    }
}