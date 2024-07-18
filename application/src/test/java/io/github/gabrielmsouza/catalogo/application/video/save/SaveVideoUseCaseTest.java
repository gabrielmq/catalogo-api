package io.github.gabrielmsouza.catalogo.application.video.save;

import io.github.gabrielmsouza.catalogo.application.UseCaseTest;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.domain.video.Rating;
import io.github.gabrielmsouza.catalogo.domain.video.Video;
import io.github.gabrielmsouza.catalogo.domain.video.VideoGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveVideoUseCaseTest extends UseCaseTest {
    @InjectMocks
    private SaveVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Test
    public void givenValidInput_whenCallsSave_shouldPersistIt() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCreatedAt = InstantUtils.now();
        final var expectedUpdatedAt = InstantUtils.now();
        final var expectedCategories = Set.of(IDUtils.uuid());
        final var expectedCastMembers = Set.of(IDUtils.uuid());
        final var expectedGenres = Set.of(IDUtils.uuid());
        final var expectedVideo = "http://video";
        final var expectedTrailer = "http://trailer";
        final var expectedBanner = "http://banner";
        final var expectedThumbnail = "http://thumb";
        final var expectedThumbnailHalf = "http://thumbhalf";

        when(videoGateway.save(any())).thenAnswer(returnsFirstArg());

        // when
        final var input = new SaveVideoUseCase.Input(
                expectedId,
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                expectedCreatedAt.toString(),
                expectedUpdatedAt.toString(),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedCategories,
                expectedCastMembers,
                expectedGenres
        );

        final var actualOutput = this.useCase.execute(input);

        // then
        Assertions.assertNotNull(actualOutput);
        assertEquals(expectedId, actualOutput.id());

        var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).save(captor.capture());

        var actualVideo = captor.getValue();
        Assertions.assertNotNull(actualVideo);
        assertEquals(expectedId, actualVideo.id());
        assertEquals(expectedCreatedAt, actualVideo.createdAt());
        assertEquals(expectedUpdatedAt, actualVideo.updatedAt());
        assertEquals(expectedTitle, actualVideo.title());
        assertEquals(expectedDescription, actualVideo.description());
        assertEquals(expectedLaunchedAt, actualVideo.launchedAt());
        assertEquals(expectedDuration, actualVideo.duration());
        assertEquals(expectedOpened, actualVideo.opened());
        assertEquals(expectedPublished, actualVideo.published());
        assertEquals(expectedRating, actualVideo.rating());
        assertEquals(expectedCategories, actualVideo.categories());
        assertEquals(expectedGenres, actualVideo.genres());
        assertEquals(expectedCastMembers, actualVideo.castMembers());
        assertEquals(expectedVideo, actualVideo.video());
        assertEquals(expectedTrailer, actualVideo.trailer());
        assertEquals(expectedBanner, actualVideo.banner());
        assertEquals(expectedThumbnail, actualVideo.thumbnail());
        assertEquals(expectedThumbnailHalf, actualVideo.thumbnailHalf());
    }

    @Test
    public void givenNullInput_whenCallsSave_shouldReturnError() {
        // given
        final SaveVideoUseCase.Input input = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'SaveVideoUseCase.Input' cannot be null";

        // when
        final var actualError = assertThrows(DomainException.class, () -> this.useCase.execute(input));

        // then
        assertEquals(expectedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

        verify(videoGateway, times(0)).save(any());
    }

    @Test
    public void givenInvalidId_whenCallsSave_shouldReturnError() {
        // given
        final String expectedId = null;
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCreatedAt = InstantUtils.now();
        final var expectedUpdatedAt = InstantUtils.now();
        final var expectedCategories = Set.of(IDUtils.uuid());
        final var expectedCastMembers = Set.of(IDUtils.uuid());
        final var expectedGenres = Set.of(IDUtils.uuid());
        final var expectedVideo = "http://video";
        final var expectedTrailer = "http://trailer";
        final var expectedBanner = "http://banner";
        final var expectedThumbnail = "http://thumb";
        final var expectedThumbnailHalf = "http://thumbhalf";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var input = new SaveVideoUseCase.Input(
                expectedId,
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                expectedCreatedAt.toString(),
                expectedUpdatedAt.toString(),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedCategories,
                expectedCastMembers,
                expectedGenres
        );

        final var actualError = assertThrows(
                DomainException.class,
                () -> this.useCase.execute(input)
        );

        // then
        assertEquals(expectedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

        verify(videoGateway, times(0)).save(any());
    }
}
