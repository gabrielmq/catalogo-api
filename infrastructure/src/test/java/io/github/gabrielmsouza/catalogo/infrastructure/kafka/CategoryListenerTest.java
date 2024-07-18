package io.github.gabrielmsouza.catalogo.infrastructure.kafka;

import io.github.gabrielmsouza.catalogo.AbstractEmbeddedKafkaTest;
import io.github.gabrielmsouza.catalogo.application.category.delete.DeleteCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.infrastructure.category.CategoryClient;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.MessageValue;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.Operation;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.ValuePayload;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.*;

@Disabled
public class CategoryListenerTest extends AbstractEmbeddedKafkaTest {
    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private SaveCategoryUseCase saveCategoryUseCase;

    @MockBean
    private CategoryClient categoryClient;

    @SpyBean
    private CategoryListener categoryListener;

    @Value("${kafka.consumers.categories.topics}")
    private String categoryTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @Test
    public void testCategoriesTopics() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.categories";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.categories-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.categories-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.categories-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.categories-dlt";

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
        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aulasEvent, null, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return aulas;
        }).when(saveCategoryUseCase).execute(any());
        doReturn(Optional.of(aulas)).when(categoryClient).categoryOfId(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(categoryClient).categoryOfId(eq(aulas.id()));
        verify(saveCategoryUseCase).execute(eq(aulas));
    }

    @Test
    void givenUpdateOperationWhenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aulasEvent, aulasEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return aulas;
        }).when(saveCategoryUseCase).execute(any());
        doReturn(Optional.of(aulas)).when(categoryClient).categoryOfId(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(categoryClient).categoryOfId(eq(aulas.id()));
        verify(saveCategoryUseCase).execute(eq(aulas));
    }

    @Test
    void givenDeleteOperationWhenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(null, aulasEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(deleteCategoryUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(deleteCategoryUseCase).execute(eq(aulas.id()));
    }

    @Test
    void givenInvalidResponsesFromHandler_thenShouldRetryUntilGoesToDLT() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.categories";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.categories-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.categories-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.categories-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.categories-dlt";
        final var expectedMaxAttempts = 4;
        final var expectedMaxDLTAttempts = 1;

        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aulasEvent, aulasEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(5);
        doAnswer(t -> {
            latch.countDown();
            if (latch.getCount() > 0) {
                throw new RuntimeException("BOOM!");
            }
            return null;
        }).when(deleteCategoryUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message)).get(10, TimeUnit.SECONDS);

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(categoryListener, times(expectedMaxAttempts)).onMessage(eq(message), metadata.capture());

        final var metas = metadata.getAllValues();
        assertEquals(expectedMainTopic, metas.get(0).topic());
        assertEquals(expectedRetry0Topic, metas.get(1).topic());
        assertEquals(expectedRetry1Topic, metas.get(2).topic());
        assertEquals(expectedRetry2Topic, metas.get(3).topic());

        verify(categoryListener, times(expectedMaxDLTAttempts)).onDLTMessage(eq(message), metadata.capture());
        assertEquals(expectedDLTTopic, metadata.getValue().topic());
    }
}
