package io.github.gabrielmsouza.catalogo.domain.category;

import io.github.gabrielmsouza.catalogo.domain.UnitTest;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallWith_thenInstantiateACategory() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        // when
        final var actualCategory = Category.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // then
        assertNotNull(actualCategory);
        assertNotNull(actualCategory.id());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertEquals(expectedDates, actualCategory.createdAt());
        assertEquals(expectedDates, actualCategory.updatedAt());
        assertEquals(expectedDates, actualCategory.deletedAt());
    }

    @Test
    public void givenAValidParams_whenCallWithCategory_thenInstantiateACategory() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        final var aCategory = Category.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualCategory = Category.with(aCategory);

        // then
        assertEquals(aCategory.id(), actualCategory.id());
        assertEquals(aCategory.name(), actualCategory.name());
        assertEquals(aCategory.description(), actualCategory.description());
        assertEquals(aCategory.active(), actualCategory.active());
        assertEquals(aCategory.createdAt(), actualCategory.createdAt());
        assertEquals(aCategory.updatedAt(), actualCategory.updatedAt());
        assertEquals(aCategory.deletedAt(), actualCategory.deletedAt());
    }

    @Test
    public void givenAnInvalidNullId_whenCallNewCategoryWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedId = null;
        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        final var actualCategory = Category.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyId_whenCallNewCategoryWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = "";

        final var expectedId = "";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        final var actualCategory = Category.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullName_whenCallNewCategoryWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = null;

        final var expectedId = IDUtils.uuid();
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualCategory = Category.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewCategoryWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = "";

        final var expectedId = IDUtils.uuid();
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualCategory = Category.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}
