package io.github.gabrielmsouza.catalogo.infrastructure.video;

import io.github.gabrielmsouza.catalogo.infrastructure.authentication.GetClientCredentials;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.annontations.Videos;
import io.github.gabrielmsouza.catalogo.infrastructure.utils.HttpClient;
import io.github.gabrielmsouza.catalogo.infrastructure.video.models.VideoDTO;
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
@CacheConfig(cacheNames = "admin-videos")
public class VideoRestClient implements VideoClient, HttpClient {
    public static final String NAMESPACE = "videos";

    private final RestClient restClient;
    private final GetClientCredentials clientCredentials;

    public VideoRestClient(
            @Videos final RestClient restClient,
            final GetClientCredentials clientCredentials
    ) {
        this.restClient = Objects.requireNonNull(restClient);
        this.clientCredentials = Objects.requireNonNull(clientCredentials);
    }

    @Override
    @Cacheable(key = "#videoId")
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    public Optional<VideoDTO> videoOfId(final String videoId) {
        final var token = this.clientCredentials.retrieve();
        final Supplier<VideoDTO> request = () -> this.restClient.get()
                .uri("/{id}", videoId)
                .header(AUTHORIZATION, "bearer " + token)
                .retrieve()
                .onStatus(isNotFound, notFoundHandler(videoId))
                .onStatus(is5xx, a5xxHandler(videoId))
                .body(VideoDTO.class);
        return doGet(videoId, request);
    }

    @Override
    public String namespace() {
        return NAMESPACE;
    }
}
