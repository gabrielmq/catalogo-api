package io.github.gabrielmsouza.catalogo.domain.castmember;

import io.github.gabrielmsouza.catalogo.domain.UnitTest;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CastMemberTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallWith_thenInstantiateACastMember() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "John Doe";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedDates = InstantUtils.now();

        // when
        final var actualCastMember = CastMember.with(
                expectedId,
                expectedName,
                expectedType,
                expectedDates,
                expectedDates
        );

        // then
        assertNotNull(actualCastMember.id());
        assertEquals(expectedName, actualCastMember.name());
        assertEquals(expectedType, actualCastMember.type());
        assertEquals(expectedDates, actualCastMember.createdAt());
        assertEquals(expectedDates, actualCastMember.updatedAt());
    }

    @Test
    public void givenAValidParams_whenCallWithCastMember_thenInstantiateACastMember() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "John Doe";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedDates = InstantUtils.now();

        final var aCatMember = CastMember.with(
                expectedId,
                expectedName,
                expectedType,
                expectedDates,
                expectedDates
        );

        // when
        final var actualCastMember = CastMember.with(aCatMember);

        // then
        assertEquals(aCatMember.id(), actualCastMember.id());
        assertEquals(aCatMember.name(), actualCastMember.name());
        assertEquals(aCatMember.type(), actualCastMember.type());
        assertEquals(aCatMember.createdAt(), actualCastMember.createdAt());
        assertEquals(aCatMember.updatedAt(), actualCastMember.updatedAt());
    }

    @Test
    public void givenAnInvalidNullId_whenCallNewCastMemberWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedId = null;
        final var expectedName = "John Doe";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        final var actualCastMember = CastMember.with(
                expectedId,
                expectedName,
                expectedType,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCastMember.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyId_whenCallNewCastMemberWithAndValidate_thenShouldReceiveError() {
        // given

        final var expectedId = "";
        final var expectedName = "John Doe";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        final var actualCastMember = CastMember.with(
                expectedId,
                expectedName,
                expectedType,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCastMember.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullName_whenCallNewCastMemberWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = null;

        final var expectedId = IDUtils.uuid();
        final var expectedType = CastMemberType.ACTOR;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualCastMember = CastMember.with(
                expectedId,
                expectedName,
                expectedType,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCastMember.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewCastMEmberWithAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = "";

        final var expectedId = IDUtils.uuid();
        final var expectedType = CastMemberType.ACTOR;
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualCastMember = CastMember.with(
                expectedId,
                expectedName,
                expectedType,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException =
                assertThrows(DomainException.class, () -> actualCastMember.validate(new ThrowsValidationHandler()));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullType_whenCallWithAndValidate_thenShouldReceiveError() {
        // given
        final var expectedID = IDUtils.uuid();
        final var expectedName = "John Doe";
        final CastMemberType expectedType = null;
        final var expectedDates = InstantUtils.now();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        // when
        final var actualMember =
                CastMember.with(expectedID, expectedName, expectedType, expectedDates, expectedDates);

        final var actualException =
                assertThrows(DomainException.class, () -> actualMember.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}