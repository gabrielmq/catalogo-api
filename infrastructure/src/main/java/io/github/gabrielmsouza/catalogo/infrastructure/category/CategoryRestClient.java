package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.GetClientCredentials;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryDTO;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.annontations.Categories;
import io.github.gabrielmsouza.catalogo.infrastructure.utils.HttpClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@CacheConfig(cacheNames = "admin-categories")
public class CategoryRestClient implements CategoryClient, HttpClient {
    public static final String NAMESPACE = "categories";

    private final RestClient restClient;
    private final GetClientCredentials clientCredentials;

    public CategoryRestClient(
            final @Categories RestClient categoryHttpClient,
            final GetClientCredentials clientCredentials
    ) {
        this.restClient = Objects.requireNonNull(categoryHttpClient);
        this.clientCredentials = Objects.requireNonNull(clientCredentials);
    }

    @Override
    @Cacheable(key = "#categoryId")
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    public Optional<Category> categoryOfId(final String categoryId) {
        final var token = this.clientCredentials.retrieve();
        final Supplier<CategoryDTO> request = () ->
                this.restClient
                        .get()
                        .uri("/{id}", categoryId)
                        .header(AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .onStatus(isNotFound, notFoundHandler(categoryId))
                        .onStatus(is5xx, a5xxHandler(categoryId))
                        .body(CategoryDTO.class);

        return doGet(categoryId, request).map(CategoryDTO::toCategory);
    }

    @Override
    public String namespace() {
        return NAMESPACE;
    }
}
