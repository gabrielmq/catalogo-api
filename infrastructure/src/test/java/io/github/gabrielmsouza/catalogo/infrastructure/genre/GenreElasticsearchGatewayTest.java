package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.AbstractElasticsearchTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.persistence.GenreDocument;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GenreElasticsearchGatewayTest extends AbstractElasticsearchTest {
    @Autowired
    private GenreElasticsearchGateway gateway;

    @Autowired
    private GenreRepository repository;

    @Test
    void testInjection() {
        assertNotNull(gateway);
        assertNotNull(repository);
    }

    @Test
    void givenActiveGenreWithCategories_whenCallsSave_thenShouldPersistIt() {
        // given
        final var tech = Genre.with(
                IDUtils.uuid(),
                "Tech",
                true,
                Set.of("c1", "c2"),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        // when
        final var actualOutput = this.gateway.save(tech);

        // then
        assertEquals(tech, actualOutput);

        final var actualMember = this.repository.findById(tech.id()).get();
        assertEquals(tech.id(), actualMember.id());
        assertEquals(tech.name(), actualMember.name());
        assertEquals(tech.active(), actualMember.active());
        assertEquals(tech.categories(), actualMember.categories());
        assertEquals(tech.createdAt(), actualMember.createdAt());
        assertEquals(tech.updatedAt(), actualMember.updatedAt());
        assertEquals(tech.deletedAt(), actualMember.deletedAt());
    }

    @Test
    void givenInactiveGenreWithoutCategories_whenCallsSave_thenShouldPersistIt() {
        // given
        final var business = Genre.with(
                IDUtils.uuid(),
                "Business",
                false,
                Set.of(),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        // when
        final var actualOutput = this.gateway.save(business);

        // then
        assertEquals(business, actualOutput);

        final var actualMember = this.repository.findById(business.id()).get();
        assertEquals(business.id(), actualMember.id());
        assertEquals(business.name(), actualMember.name());
        assertEquals(business.active(), actualMember.active());
        assertEquals(business.categories(), actualMember.categories());
        assertEquals(business.createdAt(), actualMember.createdAt());
        assertEquals(business.updatedAt(), actualMember.updatedAt());
        assertEquals(business.deletedAt(), actualMember.deletedAt());
    }

    @Test
    void givenValidId_whenCallsDeleteById_thenShouldDeleteIt() {
        // given
        final var business = Fixture.Genres.business();

        this.repository.save(GenreDocument.from(business));

        final var expectedId = business.id();

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
    void givenActiveGenreWithCategories_whenCallsFindById_thenShouldRetrieveIt() {
        // given
        final var tech = Genre.with(
                IDUtils.uuid(),
                "Tech",
                true,
                Set.of("c1", "c2"),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        this.repository.save(GenreDocument.from(tech));

        final var expectedId = tech.id();

        assertTrue(this.repository.existsById(expectedId));

        // when
        final var actualOutput = this.gateway.findById(expectedId).get();

        // then
        assertEquals(tech.id(), actualOutput.id());
        assertEquals(tech.name(), actualOutput.name());
        assertEquals(tech.active(), actualOutput.active());
        assertEquals(tech.categories(), actualOutput.categories());
        assertEquals(tech.createdAt(), actualOutput.createdAt());
        assertEquals(tech.updatedAt(), actualOutput.updatedAt());
        assertEquals(tech.deletedAt(), actualOutput.deletedAt());
    }

    @Test
    void givenInactiveGenreWithoutCategories_whenCallsFindById_thenShouldRetrieveIt() {
        // given
        final var business = Genre.with(
                IDUtils.uuid(),
                "Business",
                false,
                Set.of(),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        this.repository.save(GenreDocument.from(business));

        final var expectedId = business.id();

        assertTrue(this.repository.existsById(expectedId));

        // when
        final var actualOutput = this.gateway.findById(expectedId).get();

        // then
        assertEquals(business.id(), actualOutput.id());
        assertEquals(business.name(), actualOutput.name());
        assertEquals(business.active(), actualOutput.active());
        assertEquals(business.categories(), actualOutput.categories());
        assertEquals(business.createdAt(), actualOutput.createdAt());
        assertEquals(business.updatedAt(), actualOutput.updatedAt());
        assertEquals(business.deletedAt(), actualOutput.deletedAt());
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
    public void givenEmptyGenres_whenCallsFindAll_thenShouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedCategories = Set.<String>of();

        final var aQuery = new GenreSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                expectedCategories
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
            "mar,0,10,1,1,Marketing",
            "tec,0,10,1,1,Technology"
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
        mockGenres();

        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedCategories = Set.<String>of();

        final var aQuery = new GenreSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                expectedCategories
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
            "c123,0,10,1,1,Marketing",
            "c456,0,10,1,1,Technology",
            ",0,10,3,3,Business"
    })
    public void givenValidCategory_whenCallsFindAll_thenShouldReturnElementsFiltered(
            final String categories,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockGenres();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedCategories = categories != null ? Set.of(categories) : Set.<String>of();

        final var aQuery = new GenreSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                expectedCategories
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
            "name,asc,0,10,3,3,Business",
            "name,desc,0,10,3,3,Technology",
            "created_at,asc,0,10,3,3,Technology",
            "created_at,desc,0,10,3,3,Marketing",
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
        mockGenres();

        final var expectedTerms = "";
        final var expectedCategories = Set.<String>of();

        final var aQuery = new GenreSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                expectedCategories
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
            "0,1,1,3,Business",
            "1,1,1,3,Marketing",
            "2,1,1,3,Technology",
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
        mockGenres();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedCategories = Set.<String>of();

        final var aQuery = new GenreSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                expectedCategories
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

    @Test
    public void givenValidIds_whenCallsFindAllById_shouldReturnElements() {
        // given
        final var tech = this.repository.save(GenreDocument.from(Fixture.Genres.tech()));
        this.repository.save(GenreDocument.from(Fixture.Genres.business()));
        final var marketing = this.repository.save(GenreDocument.from(Fixture.Genres.marketing()));

        final var expectedSize = 2;
        final var expectedIds = Set.of(tech.id(), marketing.id());

        // when
        final var actualOutput = this.gateway.findAllById(expectedIds);

        // then
        assertEquals(expectedSize, actualOutput.size());

        final var actualIds = actualOutput.stream().map(Genre::id).toList();
        assertTrue(expectedIds.containsAll(actualIds));
    }

    @Test
    public void givenNullIds_whenCallsFindAllById_shouldReturnEmpty() {
        // given
        final var expectedItems = List.of();
        final Set<String> expectedIds = null;

        // when
        final var actualOutput = this.gateway.findAllById(expectedIds);

        // then
        assertEquals(expectedItems, actualOutput);
    }

    @Test
    public void givenEmptyIds_whenCallsFindAllById_shouldReturnEmpty() {
        // given
        final var expectedItems = List.of();
        final Set<String> expectedIds = Set.of();

        // when
        final var actualOutput = this.gateway.findAllById(expectedIds);

        // then
        assertEquals(expectedItems, actualOutput);
    }

    private void mockGenres() {
        this.repository.save(GenreDocument.from(Fixture.Genres.tech()));
        this.repository.save(GenreDocument.from(Fixture.Genres.business()));
        this.repository.save(GenreDocument.from(Fixture.Genres.marketing()));
    }
}