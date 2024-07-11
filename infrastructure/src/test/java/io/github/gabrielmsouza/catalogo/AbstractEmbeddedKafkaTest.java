package io.github.gabrielmsouza.catalogo;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.WebServerConfiguration;
import io.github.gabrielmsouza.catalogo.infrastructure.kafka.models.connect.Source;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

@Tag("integrationTest")
@ActiveProfiles("test-integration")
@SpringBootTest(
        classes = { WebServerConfiguration.class, IntegrationTestConfiguration.class },
        properties = { "kafka.bootstrap-servers=${spring.embedded.kafka.brokers}" }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableAutoConfiguration(exclude = ElasticsearchRepositoriesAutoConfiguration.class)
@EmbeddedKafka(partitions = 1,  brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public abstract class AbstractEmbeddedKafkaTest {
    private Producer<String, String> producer;
    private AdminClient admin;

    @Autowired
    protected EmbeddedKafkaBroker kafkaBroker;

    @BeforeAll
    void init() {
        this.admin =
            AdminClient.create(Collections.singletonMap(BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString()));

        this.producer = new DefaultKafkaProducerFactory<>(
                KafkaTestUtils.producerProps(kafkaBroker),
                new StringSerializer(),
                new StringSerializer()
        ).createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }

    protected Producer<String, String> producer() {
        return producer;
    }

    protected AdminClient admin() {
        return admin;
    }

    protected Source aSource() {
        return new Source("admin_mysql", "admin_catalogo", "categories");
    }
}
