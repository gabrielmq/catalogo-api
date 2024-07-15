package io.github.gabrielmsouza.catalogo.infrastructure.kafka;

import io.github.gabrielmsouza.catalogo.AbstractEmbeddedKafkaTest;
import io.github.gabrielmsouza.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.save.SaveCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CastMemberListenerTest extends AbstractEmbeddedKafkaTest {
    @MockBean
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private SaveCastMemberUseCase saveCastMemberUseCase;

    @SpyBean
    private CastMemberListener castMemberListener;

    @Value("${kafka.consumers.cast-members.topics}")
    private String castMemberTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @Test
    public void testCastMemberTopics() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.cast_members";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.cast_members-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.cast_members-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.cast_members-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.cast_members-dlt";

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
    void givenCreateOperation_whenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var actor = Fixture.CastMembers.actor();
        final var castMemberEvent = CastMemberEvent.from(actor);

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(castMemberEvent, null, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return actor;
        }).when(this.saveCastMemberUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(this.castMemberTopic, message)).get(10, TimeUnit.SECONDS);
        producer().flush();

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(this.saveCastMemberUseCase).execute(refEq(actor, "createdAt", "updatedAt"));
    }

    @Test
    void givenUpdateOperation_whenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var actor = Fixture.CastMembers.actor();
        final var castMemberEvent = CastMemberEvent.from(actor);

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(castMemberEvent, castMemberEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return actor;
        }).when(this.saveCastMemberUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(this.castMemberTopic, message)).get(10, TimeUnit.SECONDS);
        producer().flush();

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(this.saveCastMemberUseCase).execute(refEq(actor, "createdAt", "updatedAt"));
    }

    @Test
    void givenDeleteOperation_whenProcessGoesOk_thenShouldEndTheOperation() throws Exception {
        // given
        final var actor = Fixture.CastMembers.actor();
        final var castMemberEvent = CastMemberEvent.from(actor);

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(castMemberEvent, castMemberEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(1);
        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(this.deleteCastMemberUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(this.castMemberTopic, message)).get(10, TimeUnit.SECONDS);
        producer().flush();

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(this.deleteCastMemberUseCase).execute(eq(actor.id()));
    }

    @Test
    void givenInvalidResponsesFromHandler_thenShouldRetryUntilGoesToDLT() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.cast_members";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.cast_members-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.cast_members-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.cast_members-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.cast_members-dlt";
        final var expectedMaxAttempts = 4;
        final var expectedMaxDLTAttempts = 1;

        final var actor = Fixture.CastMembers.actor();
        final var castMemberEvent = CastMemberEvent.from(actor);

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(castMemberEvent, castMemberEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(5);
        doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("BOOM!");
        }).when(this.deleteCastMemberUseCase).execute(any());

        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(this.castMemberListener).onDLTMessage(any(), any());

        // when
        producer().send(new ProducerRecord<>(this.castMemberTopic, message)).get(10, TimeUnit.SECONDS);
        producer().flush();

        assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(this.castMemberListener, times(expectedMaxAttempts)).onMessage(eq(message), metadata.capture());

        final var metas = metadata.getAllValues();
        assertEquals(expectedMainTopic, metas.get(0).topic());
        assertEquals(expectedRetry0Topic, metas.get(1).topic());
        assertEquals(expectedRetry1Topic, metas.get(2).topic());
        assertEquals(expectedRetry2Topic, metas.get(3).topic());

        verify(this.castMemberListener, times(expectedMaxDLTAttempts)).onDLTMessage(eq(message), metadata.capture());
        assertEquals(expectedDLTTopic, metadata.getValue().topic());
    }
}