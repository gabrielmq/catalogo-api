package io.github.gabrielmsouza.catalogo.infrastructure.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.gabrielmsouza.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.save.SaveCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryEvent;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.MessageValue;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.ValuePayload;
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
public class CastMemberListener {
    private static final Logger LOG = LoggerFactory.getLogger(CastMemberListener.class);

    private static final TypeReference<MessageValue<CastMemberEvent>> CAST_MEMBER_MESSAGE = new TypeReference<>() {
    };

    private final SaveCastMemberUseCase saveCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;

    public CastMemberListener(
            final SaveCastMemberUseCase saveCastMemberUseCase,
            final DeleteCastMemberUseCase deleteCastMemberUseCase
    ) {
        this.saveCastMemberUseCase = Objects.requireNonNull(saveCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
    }

    @KafkaListener(
            id = "${kafka.consumers.cast-members.id}",
            groupId = "${kafka.consumers.cast-members.group-id}",
            topics = "${kafka.consumers.cast-members.topics}",
            containerFactory = "kafkaListenerFactory",
            concurrency = "${kafka.consumers.cast-members.concurrency}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.cast-members.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            attempts = "${kafka.consumers.cast-members.max-attempts}",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(@Payload(required = false) final String payload, final ConsumerRecordMetadata metadata) {
        if (Objects.isNull(payload)) {
            LOG.info("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: EMPTY", metadata.topic(), metadata.partition(), metadata.offset());
            return;
        }
        LOG.info("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
        final var message = Json.readValue(payload, CAST_MEMBER_MESSAGE).payload();
        final var operation = message.operation();
        if (operation.isDelete()) {
            this.deleteCastMemberUseCase.execute(message.before().id());
            return;
        }
        this.saveCastMemberUseCase.execute(message.after().toCastMember());
    }

    @DltHandler
    public void onDLTMessage(@Payload final String payload, final ConsumerRecordMetadata metadata) {
        LOG.warn("Message received from Kafka at DLT [topic:{}] [partition:{}] [offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
    }
}
