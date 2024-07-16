package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.infrastructure.authentication.GetClientCredentials;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.annontations.Genres;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreDTO;
import io.github.gabrielmsouza.catalogo.infrastructure.utils.HttpClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@CacheConfig(cacheNames = "admin-genres")
public class GenreRestClient implements GenreClient, HttpClient {
    public static final String NAMESPACE = "genres";

    private final RestClient restClient;
    private final GetClientCredentials clientCredentials;

    public GenreRestClient(
            @Genres final RestClient restClient,
            final GetClientCredentials clientCredentials
    ) {
        this.restClient = Objects.requireNonNull(restClient);
        this.clientCredentials = Objects.requireNonNull(clientCredentials);
    }

    @Override
    @Cacheable(key = "#genreId")
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    public Optional<GenreDTO> genreOfId(final String genreId) {
        final var token = this.clientCredentials.retrieve();
        final Supplier<GenreDTO> request = () -> this.restClient.get()
                .uri("/{id}", genreId)
                .header(AUTHORIZATION, "bearer " + token)
                .retrieve()
                .onStatus(isNotFound, notFoundHandler(genreId))
                .onStatus(is5xx, a5xxHandler(genreId))
                .body(GenreDTO.class);
        return doGet(genreId, request);
    }

    @Override
    public String namespace() {
        return NAMESPACE;
    }
}
