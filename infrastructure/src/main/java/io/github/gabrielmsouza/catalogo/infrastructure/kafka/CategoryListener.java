package io.github.gabrielmsouza.catalogo.infrastructure.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.gabrielmsouza.catalogo.application.category.delete.DeleteCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.infrastructure.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
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
public class CategoryListener {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryListener.class);

    private static final TypeReference<MessageValue<CategoryEvent>> CATEGORY_MESSAGE = new TypeReference<>() {
    };

    private final SaveCategoryUseCase saveCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CategoryGateway categoryGateway;

    public CategoryListener(
            final SaveCategoryUseCase saveCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase,
            final CategoryGateway categoryGateway
    ) {
        this.saveCategoryUseCase = Objects.requireNonNull(saveCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @KafkaListener(
            id = "${kafka.consumers.categories.id}",
            groupId = "${kafka.consumers.categories.group-id}",
            topics = "${kafka.consumers.categories.topics}",
            containerFactory = "kafkaListenerFactory",
            concurrency = "${kafka.consumers.categories.concurrency}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.categories.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            attempts = "${kafka.consumers.categories.max-attempts}",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(@Payload(required = false) final String payload, final ConsumerRecordMetadata metadata) {
        if (Objects.isNull(payload)) {
            LOG.info("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: EMPTY", metadata.topic(), metadata.partition(), metadata.offset());
            return;
        }

        LOG.info("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
        final var message = Json.readValue(payload, CATEGORY_MESSAGE).payload();
        final var operation = message.operation();
        if (operation.isDelete()) {
            this.deleteCategoryUseCase.execute(message.before().id());
            return;
        }

        this.categoryGateway.categoryOfId(message.after().id())
                .ifPresentOrElse(
                        this.saveCategoryUseCase::execute,
                        () -> LOG.warn("Category was not found {}", message.after().id())
                );
    }

    @DltHandler
    public void onDLTMessage(@Payload final String payload, final ConsumerRecordMetadata metadata) {
        LOG.warn("Message received from Kafka at DLT [topic:{}] [partition:{}] [offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
        final var message = Json.readValue(payload, CATEGORY_MESSAGE).payload();
        final var operation = message.operation();
        if (operation.isDelete()) {
            this.deleteCategoryUseCase.execute(message.before().id());
            return;
        }

        this.categoryGateway.categoryOfId(message.after().id())
                .ifPresentOrElse(
                        this.saveCategoryUseCase::execute,
                        () -> LOG.warn("Category was not found {}", message.after().id())
                );
    }
}
