package io.github.gabrielmsouza.catalogo;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.WebServerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;

@Testcontainers
@DataElasticsearchTest
@Tag("integrationTest")
@ActiveProfiles("test-integration")
@ImportTestcontainers(ElasticsearchTestContainer.class)
@SpringBootTest(classes = {
        WebServerConfiguration.class,
        IntegrationTestConfiguration.class
})
@ComponentScan(
        basePackages = "io.github.gabrielmsouza.catalogo",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ElasticsearchGateway")
        }
)
public abstract class AbstractElasticsearchTest {
    @Autowired
    private Collection<ElasticsearchRepository<?, ?>> repositories;

    @BeforeEach
    void setup() {
        this.repositories.forEach(ElasticsearchRepository::deleteAll);
    }
}
