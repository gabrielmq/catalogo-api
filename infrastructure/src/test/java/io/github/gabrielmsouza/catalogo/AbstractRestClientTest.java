package io.github.gabrielmsouza.catalogo;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.gabrielmsouza.catalogo.infrastructure.category.CategoryRestClient;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.WebServerConfiguration;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integrationTest")
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test-integration")
@SpringBootTest(classes = {
        WebServerConfiguration.class,
        IntegrationTestConfiguration.class
})
@EnableAutoConfiguration(exclude = {
        ElasticsearchRepositoriesAutoConfiguration.class,
        KafkaAutoConfiguration.class
})
public abstract class AbstractRestClientTest {

    static final String CATEGORY = CategoryRestClient.NAMESPACE;

    @Autowired
    private BulkheadRegistry bulkheadRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setup() {
        WireMock.reset();
        WireMock.resetAllRequests();
        resetAllCaches();
        List.of(CATEGORY).forEach(this::resetFaultTolerance);
    }

    protected void acquireBulkheadPermission(final String name) {
        this.bulkheadRegistry.bulkhead(name).acquirePermission();
    }

    protected void releaseBulkheadPermission(final String name) {
        this.bulkheadRegistry.bulkhead(name).releasePermission();
    }

    protected void checkCircuitBreakerState(final String name, final CircuitBreaker.State state) {
        final var cb = this.circuitBreakerRegistry.circuitBreaker(name);
        assertEquals(state, cb.getState());
    }

    protected void transitionToOpenState(final String name) {
        this.circuitBreakerRegistry.circuitBreaker(name).transitionToOpenState();
    }

    protected void transitionToCloseState(final String name) {
        this.circuitBreakerRegistry.circuitBreaker(name).transitionToClosedState();
    }

    protected Cache cache(final String name) {
        return cacheManager.getCache(name);
    }

    private void resetAllCaches() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    private void resetFaultTolerance(final String name) {
        this.circuitBreakerRegistry.circuitBreaker(name).reset();
    }
}
