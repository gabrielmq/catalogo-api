package io.github.gabrielmsouza.catalogo.application.category.save;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
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


public class SaveCategoryUseCaseTest extends UseCaseTest {
    @InjectMocks
    private SaveCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCategory_whenCallsSave_thenShouldPersistIt() {
        // given
        final var aCategory = Fixture.Categories.aulas();

        when(this.categoryGateway.save(any())).thenAnswer(returnsFirstArg());

        // when
        this.useCase.execute(aCategory);

        // then
        verify(this.categoryGateway).save(eq(aCategory));
    }

    @Test
    void givenNullCategory_whenCallsSave_thenShouldReturnError() {
        // given
        final Category aCategory = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'aCategory' cannot be null";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.categoryGateway, never()).save(any());
    }

    @Test
    void givenInvalidName_whenCallsSave_thenShouldReturnError() {
        // given
        final var aCategory = Category.with(
                IDUtils.uuid(),
                "",
                "Conteudo gravado",
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.categoryGateway, never()).save(any());
    }

    @Test
    void givenInvalidId_whenCallsSave_thenShouldReturnError() {
        // given
        final var aCategory = Category.with(
                "",
                "Aulas",
                "Conteudo gravado",
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var actualException = assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        // then
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(this.categoryGateway, never()).save(any());
    }
}
