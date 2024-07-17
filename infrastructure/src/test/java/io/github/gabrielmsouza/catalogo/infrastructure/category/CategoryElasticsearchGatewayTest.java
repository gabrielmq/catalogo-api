package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.AbstractElasticsearchTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryDocument;
import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryElasticsearchGatewayTest extends AbstractElasticsearchTest {
    @Autowired
    private CategoryElasticsearchGateway gateway;

    @Autowired
    private CategoryRepository repository;

    @Test
    void testInjection() {
        assertNotNull(gateway);
        assertNotNull(repository);
    }

    @Test
    void givenValidCategory_whenCallsSave_thenShouldPersistIt() {
        // given
        final var aulas = Fixture.Categories.aulas();

        // when
        final var actualOutput = this.gateway.save(aulas);

        // then
        assertEquals(aulas, actualOutput);

        final var actualCategory = this.repository.findById(aulas.id()).get();
        assertEquals(aulas.id(), actualCategory.id());
        assertEquals(aulas.name(), actualCategory.name());
        assertEquals(aulas.description(), actualCategory.description());
        assertEquals(aulas.active(), actualCategory.active());
        assertEquals(aulas.createdAt(), actualCategory.createdAt());
        assertEquals(aulas.updatedAt(), actualCategory.updatedAt());
        assertEquals(aulas.deletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenValidId_whenCallsDeleteById_thenShouldDeleteIt() {
        // given
        final var aulas = Fixture.Categories.aulas();

        this.repository.save(CategoryDocument.from(aulas));

        final var expectedId = aulas.id();

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
        final var aulas = Fixture.Categories.aulas();

        this.repository.save(CategoryDocument.from(aulas));

        final var expectedId = aulas.id();

        assertTrue(this.repository.existsById(expectedId));

        // when
        final var actualOutput = this.gateway.findById(expectedId).get();

        // then
        assertEquals(aulas.id(), actualOutput.id());
        assertEquals(aulas.name(), actualOutput.name());
        assertEquals(aulas.description(), actualOutput.description());
        assertEquals(aulas.active(), actualOutput.active());
        assertEquals(aulas.createdAt(), actualOutput.createdAt());
        assertEquals(aulas.updatedAt(), actualOutput.updatedAt());
        assertEquals(aulas.deletedAt(), actualOutput.deletedAt());
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
    public void givenEmptyCategories_whenCallsFindAll_thenShouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new CategorySearchQuery(
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
            "aul,0,10,1,1,Aulas",
            "liv,0,10,1,1,Lives"
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

        final var aQuery = new CategorySearchQuery(
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
            "name,asc,0,10,3,3,Aulas",
            "name,desc,0,10,3,3,Talks",
            "created_at,asc,0,10,3,3,Aulas",
            "created_at,desc,0,10,3,3,Lives",
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

        final var aQuery = new CategorySearchQuery(
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
            "0,1,1,3,Aulas",
            "1,1,1,3,Lives",
            "2,1,1,3,Talks",
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

        final var aQuery = new CategorySearchQuery(
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

    @Test
    public void givenValidIds_whenCallsFindAllById_shouldReturnElements() {
        // given
        final var aulas = this.repository.save(CategoryDocument.from(Fixture.Categories.aulas()));
        this.repository.save(CategoryDocument.from(Fixture.Categories.talks()));
        final var lives = this.repository.save(CategoryDocument.from(Fixture.Categories.lives()));

        final var expectedSize = 2;
        final var expectedIds = Set.of(aulas.id(), lives.id());

        // when
        final var actualOutput = this.gateway.findAllById(expectedIds);

        // then
        assertEquals(expectedSize, actualOutput.size());

        final var actualIds = actualOutput.stream().map(Category::id).toList();
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

    private void mockCategories() {
        this.repository.save(CategoryDocument.from(Fixture.Categories.aulas()));
        this.repository.save(CategoryDocument.from(Fixture.Categories.talks()));
        this.repository.save(CategoryDocument.from(Fixture.Categories.lives()));
    }
}
