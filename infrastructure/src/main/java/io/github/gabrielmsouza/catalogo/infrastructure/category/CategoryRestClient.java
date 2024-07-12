package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryDTO;
import io.github.gabrielmsouza.catalogo.infrastructure.utils.HttpClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class CategoryRestClient implements CategoryClient, HttpClient {
    public static final String CATEGORY = "categories";

    private final RestClient restClient;

    public CategoryRestClient(final RestClient categoryHttpClient) {
        this.restClient = Objects.requireNonNull(categoryHttpClient);
    }

    @Override
    @Retry(name = CATEGORY)
    @Bulkhead(name = CATEGORY)
    @CircuitBreaker(name = CATEGORY)
    public Optional<Category> categoryOfId(final String anId) {
        final Supplier<CategoryDTO> request = () ->
                this.restClient
                        .get()
                        .uri("/{id}", anId)
                        .retrieve()
                        .onStatus(isNotFound, notFoundHandler(anId))
                        .onStatus(is5xx, a5xxHandler(anId))
                        .body(CategoryDTO.class);

        return doGet(anId, request).map(CategoryDTO::toCategory);
    }

    @Override
    public String namespace() {
        return CATEGORY;
    }
}
