package io.github.gabrielmsouza.catalogo.infrastructure.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.gabrielmsouza.catalogo.application.category.delete.DeleteCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.delete.DeleteGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.save.SaveGenreUseCase;
import io.github.gabrielmsouza.catalogo.infrastructure.category.CategoryClient;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.GenreClient;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.MessageValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GenreListener {
    private static final Logger LOG = LoggerFactory.getLogger(GenreListener.class);

    private static final TypeReference<MessageValue<GenreEvent>> GENRE_MESSAGE = new TypeReference<>() {
    };

    private final SaveGenreUseCase saveGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final GenreClient genreClient;

    public GenreListener(
            final SaveGenreUseCase saveGenreUseCase,
            final DeleteGenreUseCase deleteGenreUseCase,
            final GenreClient genreClient
    ) {
        this.saveGenreUseCase = Objects.requireNonNull(saveGenreUseCase);
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase);
        this.genreClient = Objects.requireNonNull(genreClient);
    }

    @KafkaListener(
            id = "${kafka.consumers.genres.id}",
            groupId = "${kafka.consumers.genres.group-id}",
            topics = "${kafka.consumers.genres.topics}",
            containerFactory = "kafkaListenerFactory",
            concurrency = "${kafka.consumers.genres.concurrency}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.genres.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            attempts = "${kafka.consumers.genres.max-attempts}",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(@Payload(required = false) final String payload, final ConsumerRecordMetadata metadata) {
        if (Objects.isNull(payload)) {
            LOG.info("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: EMPTY", metadata.topic(), metadata.partition(), metadata.offset());
            return;
        }

        LOG.info("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
        final var message = Json.readValue(payload, GENRE_MESSAGE).payload();
        final var operation = message.operation();
        if (operation.isDelete()) {
            this.deleteGenreUseCase.execute(message.before().id());
            return;
        }

        this.genreClient.genreOfId(message.after().id())
                .map(it -> new SaveGenreUseCase.Input(
                        it.id(),
                        it.name(),
                        it.isActive(),
                        it.categoriesId(),
                        it.createdAt(),
                        it.updatedAt(),
                        it.deletedAt()
                ))
                .ifPresentOrElse(
                        this.saveGenreUseCase::execute,
                        () -> LOG.warn("Genre was not found {}", message.after().id())
                );
    }

    @DltHandler
    public void onDLTMessage(@Payload final String payload, final ConsumerRecordMetadata metadata) {
        LOG.warn("Message received from Kafka at DLT [topic:{}] [partition:{}] [offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
    }
}
