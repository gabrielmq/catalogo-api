package io.github.gabrielmsouza.catalogo.application.category.delete;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DeleteCategoryUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DeleteCategoryUseCase useCase;

    @Mock
    private CategoryGateway gateway;

    @Test
    void givenValidId_whenCallsDelete_thenShouldBeOk() {
        // given
        final var aulas = Fixture.Categories.aulas();
        final var expectedId = aulas.id();

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
