package io.github.gabrielmsouza.catalogo.application.castmember.save;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberType;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class SaveCastMemberUseCaseTest extends UseCaseTest {
    @InjectMocks
    private SaveCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCastMember_whenCallsSave_thenShouldPersistIt() {
        // given
        final var aCastMember = Fixture.CastMembers.actor();

        when(this.castMemberGateway.save(any())).thenAnswer(returnsFirstArg());

        // when
        this.useCase.execute(aCastMember);

        // then
        verify(this.castMemberGateway).save(eq(aCastMember));
    }

    @Test
    void givenNullCategory_whenCallsSave_thenShouldReturnError() {
        // given
        final CastMember aCastMember = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'aCastMember' cannot be null";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCastMember));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.castMemberGateway, never()).save(any());
    }

    @Test
    void givenInvalidName_whenCallsSave_thenShouldReturnError() {
        // given
        final var aCastMember = CastMember.with(
                IDUtils.uuid(),
                "",
                CastMemberType.ACTOR,
                InstantUtils.now(),
                InstantUtils.now()
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCastMember));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.castMemberGateway, never()).save(any());
    }

    @Test
    void givenInvalidId_whenCallsSave_thenShouldReturnError() {
        // given
        final var aCastMember = CastMember.with(
                "",
                Fixture.name(),
                CastMemberType.ACTOR,
                InstantUtils.now(),
                InstantUtils.now()
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCastMember));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.castMemberGateway, never()).save(any());
    }

    @Test
    void givenInvalidType_whenCallsSave_thenShouldReturnError() {
        // given
        final var aCastMember = CastMember.with(
                IDUtils.uuid(),
                Fixture.name(),
                null,
                InstantUtils.now(),
                InstantUtils.now()
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCastMember));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.castMemberGateway, never()).save(any());
    }
}
