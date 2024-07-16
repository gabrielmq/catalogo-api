package io.github.gabrielmsouza.catalogo.application.genre.save;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SaveGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private SaveGenreUseCase useCase;

    @Mock
    private GenreGateway gateway;

    @Test
    void givenAValidGenre_whenCallsSave_thenShouldPersistIt() {
        // given
        final var expectedID = UUID.randomUUID().toString();
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("c1", "c2");
        final var expectedDates = InstantUtils.now();

        when(gateway.save(any())).thenAnswer(returnsFirstArg());

        final var input = new SaveGenreUseCase.Input(
                expectedID,
                expectedName,
                expectedIsActive,
                expectedCategories,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualOutput = this.useCase.execute(input);

        // then
        assertNotNull(actualOutput);
        assertEquals(expectedID, actualOutput.id());

        var captor = ArgumentCaptor.forClass(Genre.class);

        verify(gateway, times(1)).save(captor.capture());

        var actualGenre = captor.getValue();
        assertNotNull(actualGenre);
        assertEquals(expectedID, actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedCategories, actualGenre.categories());
        assertEquals(expectedIsActive, actualGenre.active());
        assertEquals(expectedDates, actualGenre.createdAt());
        assertEquals(expectedDates, actualGenre.updatedAt());
        assertEquals(expectedDates, actualGenre.deletedAt());
    }

    @Test
    void givenNullInput_whenCallsSave_thenShouldReturnError() {
        // given
        final SaveGenreUseCase.Input input = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'SaveGenreUseCase.Input' cannot be null";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(input));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.gateway, never()).save(any());
    }

    @Test
    void givenInvalidName_whenCallsSave_thenShouldReturnError() {
        // given
        final var expectedID = UUID.randomUUID().toString();
        final String expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("c1", "c2");
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var input = new SaveGenreUseCase.Input(
                expectedID,
                expectedName,
                expectedIsActive,
                expectedCategories,
                expectedDates,
                expectedDates,
                expectedDates
        );

        final var actualError = assertThrows(
                DomainException.class,
                () -> this.useCase.execute(input)
        );

        // then
        assertEquals(expectedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

        verify(this.gateway, times(0)).save(any());
    }

    @Test
    void givenInvalidId_whenCallsSave_thenShouldReturnError() {
        // given
        final String expectedID = null;
        final var expectedName = "Business";
        final var expectedIsActive = true;
        final var expectedCategories = Set.of("c1", "c2");
        final var expectedDates = InstantUtils.now();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        final var input = new SaveGenreUseCase.Input(
                expectedID,
                expectedName,
                expectedIsActive,
                expectedCategories,
                expectedDates,
                expectedDates,
                expectedDates
        );

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(input));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.gateway, never()).save(any());
    }
}