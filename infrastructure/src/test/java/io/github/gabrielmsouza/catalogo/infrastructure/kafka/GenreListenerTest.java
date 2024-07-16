package io.github.gabrielmsouza.catalogo.infrastructure.kafka;

import io.github.gabrielmsouza.catalogo.AbstractEmbeddedKafkaTest;
import io.github.gabrielmsouza.catalogo.application.genre.delete.DeleteGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.save.SaveGenreUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.GenreClient;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreDTO;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.MessageValue;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.Operation;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.ValuePayload;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GenreListenerTest extends AbstractEmbeddedKafkaTest {
    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private SaveGenreUseCase saveGenreUseCase;

    @MockBean
    private GenreClient genreClient;

    @SpyBean
    private GenreListener genreListener;

    @Value("${kafka.consumers.genres.topics}")
    private String genreTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @Test
    public void testCategoriesTopics() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.genres";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.genres-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.genres-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.genres-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.genres-dlt";

        // when
        final var actualTopics = admin().listTopics().listings().get(10, TimeUnit.SECONDS)
                .stream()
                .map(TopicListing::name)
                .collect(Collectors.toSet());

        // then
        assertTrue(actualTopics.contains(expectedMainTopic));
        assertTrue(actualTopics.contains(expectedRetry0Topic));
        assertTrue(actualTopics.contains(expectedRetry1Topic));
        assertTrue(actualTopics.contains(expectedRetry2Topic));
        assertTrue(actualTopics.contains(expectedDLTTopic));
    }

    @Test
    void givenCreateOperationWhenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var tech = Fixture.Genres.tech();
        final var techEvent = new GenreEvent(tech.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(techEvent, null, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return new SaveGenreUseCase.Output(tech.id());
        }).when(saveGenreUseCase).execute(any());

        doReturn(Optional.of(GenreDTO.from(tech))).when(genreClient).genreOfId(any());

        // when
        producer().send(new ProducerRecord<>(genreTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(genreClient).genreOfId(eq(tech.id()));

        verify(saveGenreUseCase).execute(refEq(new SaveGenreUseCase.Input(
                tech.id(),
                tech.name(),
                tech.active(),
                tech.categories(),
                tech.createdAt(),
                tech.updatedAt(),
                tech.deletedAt()
        )));
    }

    @Test
    void givenUpdateOperationWhenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var tech = Fixture.Genres.tech();
        final var techEvent = new GenreEvent(tech.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(techEvent, techEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return new SaveGenreUseCase.Output(tech.id());
        }).when(saveGenreUseCase).execute(any());

        doReturn(Optional.of(GenreDTO.from(tech))).when(genreClient).genreOfId(any());

        // when
        producer().send(new ProducerRecord<>(genreTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(genreClient).genreOfId(eq(tech.id()));

        verify(saveGenreUseCase).execute(refEq(new SaveGenreUseCase.Input(
                tech.id(),
                tech.name(),
                tech.active(),
                tech.categories(),
                tech.createdAt(),
                tech.updatedAt(),
                tech.deletedAt()
        )));
    }

    @Test
    void givenDeleteOperationWhenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var tech = Fixture.Genres.tech();
        final var techEvent = new GenreEvent(tech.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(null, techEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(deleteGenreUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(genreTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(deleteGenreUseCase).execute(eq(tech.id()));
    }

    @Test
    void givenInvalidResponsesFromHandler_thenShouldRetryUntilGoesToDLT() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.genres";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.genres-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.genres-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.genres-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.genres-dlt";
        final var expectedMaxAttempts = 4;
        final var expectedMaxDLTAttempts = 1;

        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aulasEvent, aulasEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(5);
        doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("BOOM!");
        }).when(deleteGenreUseCase).execute(any());

        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(this.genreListener).onDLTMessage(any(), any());

        // when
        producer().send(new ProducerRecord<>(genreTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(genreListener, times(expectedMaxAttempts)).onMessage(eq(message), metadata.capture());

        final var metas = metadata.getAllValues();
        assertEquals(expectedMainTopic, metas.get(0).topic());
        assertEquals(expectedRetry0Topic, metas.get(1).topic());
        assertEquals(expectedRetry1Topic, metas.get(2).topic());
        assertEquals(expectedRetry2Topic, metas.get(3).topic());

        verify(genreListener, times(expectedMaxDLTAttempts)).onDLTMessage(eq(message), metadata.capture());
        assertEquals(expectedDLTTopic, metadata.getValue().topic());
    }
}