package io.github.gabrielmsouza.catalogo.domain.genre;

import io.github.gabrielmsouza.catalogo.domain.UnitTest;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest extends UnitTest {
    @Test
    public void givenAValidParams_whenCallWith_thenInstantiateAGenre() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("123", "456");
        final var expectedDates = InstantUtils.now();

        // when
        final var actualGenre = Genre.with(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCategories,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // then
        assertNotNull(actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.active());
        assertEquals(expectedCategories, actualGenre.categories());
        assertEquals(expectedDates, actualGenre.createdAt());
        assertEquals(expectedDates, actualGenre.updatedAt());
        assertEquals(expectedDates, actualGenre.deletedAt());
    }

    @Test
    public void givenAValidParams_whenCallWithGenre_thenInstantiateAGenre() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("123", "456");
        final var expectedDates = InstantUtils.now();

        // when
        final var aGenre = Genre.with(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCategories,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualGenre = Genre.with(aGenre);

        // then
        assertNotNull(actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.active());
        assertEquals(expectedCategories, actualGenre.categories());
        assertEquals(expectedDates, actualGenre.createdAt());
        assertEquals(expectedDates, actualGenre.updatedAt());
        assertEquals(expectedDates, actualGenre.deletedAt());
    }

    @Test
    public void givenNullCategories_whenCallWith_thenShouldInstantiateAGenreWithEmptyCategories() {
        // given
        final Set<String> expectedCategories = null;
        final var expectedID = UUID.randomUUID().toString();
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        // then
        final var actualGenre =
                Genre.with(expectedID, expectedName, expectedIsActive, expectedCategories, expectedDates, expectedDates, expectedDates);

        // when
        assertNotNull(actualGenre);
        assertEquals(expectedID, actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertNotNull(actualGenre.categories());
        assertTrue(actualGenre.categories().isEmpty());
        assertEquals(expectedIsActive, actualGenre.active());
        assertEquals(expectedDates, actualGenre.createdAt());
        assertEquals(expectedDates, actualGenre.updatedAt());
        assertEquals(expectedDates, actualGenre.deletedAt());
    }

    @Test
    public void givenAnInvalidNullId_whenCallNewGenreWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedId = null;
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("123", "456");
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var actualException = assertThrows(
                DomainException.class,
                () -> Genre.with(
                        expectedId,
                        expectedName,
                        expectedIsActive,
                        expectedCategories,
                        expectedDates,
                        expectedDates,
                        expectedDates
                )
        );

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyId_whenCallNewGenreWithAndValidate_thenShouldReceiveError() {
        // given
        final var expectedId = "";
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("123", "456");
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var actualException = assertThrows(
                DomainException.class,
                () -> Genre.with(
                        expectedId,
                        expectedName,
                        expectedIsActive,
                        expectedCategories,
                        expectedDates,
                        expectedDates,
                        expectedDates
                )
        );

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullName_whenCallNewGenreWithAndValidate_thenShouldReceiveError() {
        // given
        final var expectedId = IDUtils.uuid();
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("123", "456");
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var actualException = assertThrows(
                DomainException.class,
                () -> Genre.with(
                        expectedId,
                        expectedName,
                        expectedIsActive,
                        expectedCategories,
                        expectedDates,
                        expectedDates,
                        expectedDates
                )
        );

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewGenreWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = "";

        final var expectedId = IDUtils.uuid();
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("123", "456");
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var actualException = assertThrows(
                DomainException.class,
                () -> Genre.with(
                        expectedId,
                        expectedName,
                        expectedIsActive,
                        expectedCategories,
                        expectedDates,
                        expectedDates,
                        expectedDates
                )
        );

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}