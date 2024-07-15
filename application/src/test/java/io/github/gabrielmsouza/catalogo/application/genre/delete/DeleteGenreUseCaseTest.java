package io.github.gabrielmsouza.catalogo.application.genre.delete;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.application.category.delete.DeleteCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DeleteGenreUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DeleteGenreUseCase useCase;

    @Mock
    private GenreGateway gateway;

    @Test
    void givenValidId_whenCallsDelete_thenShouldBeOk() {
        // given
        final var business = Fixture.Genres.business();
        final var expectedId = business.id();

        doNothing().when(gateway).deleteById(anyString());

        // when
        assertDoesNotThrow(() -> useCase.execute(expectedId));

        // then
        verify(gateway).deleteById(eq(expectedId));
    }

    @Test
    void givenInvalidId_whenCallsDelete_thenShouldBeOk() {
        // given
        final String expectedId = null;

        // when
        assertDoesNotThrow(() -> useCase.execute(expectedId));

        // then
        verify(gateway, never()).deleteById(eq(expectedId));
    }
}
