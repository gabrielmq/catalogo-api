package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.AbstractRestClientTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.exceptions.InternalErrorException;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.ClientCredentialsManager;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreDTO;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class GenreRestClientTest extends AbstractRestClientTest {
    @Autowired
    private GenreRestClient restClient;

    @SpyBean
    private ClientCredentialsManager credentialsManager;

    @Test
    public void givenAGenre_whenReceive200FromServer_shouldBeOk() {
        // given
        final var tech = Fixture.Genres.tech();

        final var responseBody = Json.writeValueAsString(new GenreDTO(
                tech.id(),
                tech.name(),
                tech.active(),
                tech.categories(),
                tech.createdAt(),
                tech.updatedAt(),
                tech.deletedAt()
        ));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/genres/%s".formatted(tech.id())))
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("bearer %s".formatted(expectedToken)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        final var actualGenre = restClient.genreOfId(tech.id()).get();

        // then
        assertEquals(tech.id(), actualGenre.id());
        assertEquals(tech.name(), actualGenre.name());
        assertEquals(tech.active(), actualGenre.isActive());
        assertEquals(tech.categories(), actualGenre.categoriesId());
        assertEquals(tech.createdAt(), actualGenre.createdAt());
        assertEquals(tech.updatedAt(), actualGenre.updatedAt());
        assertEquals(tech.deletedAt(), actualGenre.deletedAt());

        verify(1, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(tech.id()))));
    }

    @Test
    public void givenAGenre_whenReceiveTwoCalls_shouldReturnCachedValue() {
        // given
        final var business = Fixture.Genres.business();

        final var responseBody = Json.writeValueAsString(new GenreDTO(
                business.id(),
                business.name(),
                business.active(),
                business.categories(),
                business.createdAt(),
                business.updatedAt(),
                business.deletedAt()
        ));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/genres/%s".formatted(business.id())))
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("bearer %s".formatted(expectedToken)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        restClient.genreOfId(business.id()).get();
        restClient.genreOfId(business.id()).get();
        final var actualGenre = restClient.genreOfId(business.id()).get();

        // then
        assertEquals(business.id(), actualGenre.id());
        assertEquals(business.name(), actualGenre.name());
        assertEquals(business.active(), actualGenre.isActive());
        assertEquals(business.categories(), actualGenre.categoriesId());
        assertEquals(business.createdAt(), actualGenre.createdAt());
        assertEquals(business.updatedAt(), actualGenre.updatedAt());
        assertEquals(business.deletedAt(), actualGenre.deletedAt());

        final var actualCachedValue = cache("admin-genres").get(business.id());
        assertEquals(actualGenre, actualCachedValue.get());

        verify(1, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(business.id()))));
    }

    // 5XX
    @Test
    public void givenAGenre_whenReceive5xxFromServer_shouldReturnInternalError() {
        // given
        final var expectedId = "456";
        final var expectedErrorMessage = "Error observed from genres [resourceId:%s] [status:500]".formatted(expectedId);

        final var responseBody = Json.writeValueAsString(Map.of("message", "Internal Server Error"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/genres/%s".formatted(expectedId)))
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("bearer %s".formatted(expectedToken)))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        final var actualEx = assertThrows(InternalErrorException.class, () -> restClient.genreOfId(expectedId));

        // then
        assertEquals(expectedErrorMessage, actualEx.getMessage());

        verify(2, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(expectedId))));
    }

    // 404
    @Test
    public void givenAGenre_whenReceive404NotFoundFromServer_shouldReturnEmpty() {
        // given
        final var expectedId = "123";
        final var responseBody = Json.writeValueAsString(Map.of("message", "Not found"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/genres/%s".formatted(expectedId)))
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("bearer %s".formatted(expectedToken)))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        final var actualGenre = restClient.genreOfId(expectedId);

        // then
        assertTrue(actualGenre.isEmpty());

        verify(1, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(expectedId))));
    }

    // Timeout
    @Test
    public void givenAGenre_whenReceiveTimeout_shouldReturnInternalError() {
        // given
        final var business = Fixture.Genres.business();
        final var expectedErrorMessage = "Timeout observed from genres [resourceId:%s]".formatted(business.id());

        final var responseBody = Json.writeValueAsString(new GenreDTO(
                business.id(),
                business.name(),
                business.active(),
                business.categories(),
                business.createdAt(),
                business.updatedAt(),
                business.deletedAt()
        ));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/genres/%s".formatted(business.id())))
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("bearer %s".formatted(expectedToken)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withFixedDelay(6000)
                                .withBody(responseBody)
                        )
        );

        // when
        final var actualEx = assertThrows(InternalErrorException.class, () -> restClient.genreOfId(business.id()));

        // then
        assertEquals(expectedErrorMessage, actualEx.getMessage());

        verify(2, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(business.id()))));
    }

    @Test
    public void givenAGenre_whenBulkheadIsFull_shouldReturnError() {
        // given
        final var expectedErrorMessage = "Bulkhead 'genres' is full and does not permit further calls";

        acquireBulkheadPermission(GENRE);

        // when
        final var actualEx = assertThrows(BulkheadFullException.class, () -> restClient.genreOfId("123"));

        // then
        assertEquals(expectedErrorMessage, actualEx.getMessage());

        releaseBulkheadPermission(GENRE);
    }

    @Test
    public void givenCall_whenCBIsOpen_shouldReturnError() {
        // given
        transitionToOpenState(GENRE);
        final var expectedId = "123";
        final var expectedErrorMessage = "CircuitBreaker 'genres' is OPEN and does not permit further calls";

        // when
        final var actualEx = assertThrows(CallNotPermittedException.class, () -> this.restClient.genreOfId(expectedId));

        // then
        checkCircuitBreakerState(GENRE, CircuitBreaker.State.OPEN);
        assertEquals(expectedErrorMessage, actualEx.getMessage());

        verify(0, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(expectedId))));
    }

    @Test
    public void givenServerError_whenIsMoreThanThreshold_shouldOpenCircuitBreaker() {
        // given
        final var expectedId = "123";
        final var expectedErrorMessage = "CircuitBreaker 'genres' is OPEN and does not permit further calls";

        final var responseBody = Json.writeValueAsString(Map.of("message", "Internal Server Error"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/genres/%s".formatted(expectedId)))
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("bearer %s".formatted(expectedToken)))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        assertThrows(InternalErrorException.class, () -> this.restClient.genreOfId(expectedId));
        final var actualEx = assertThrows(CallNotPermittedException.class, () -> this.restClient.genreOfId(expectedId));

        // then
        checkCircuitBreakerState(GENRE, CircuitBreaker.State.OPEN);
        assertEquals(expectedErrorMessage, actualEx.getMessage());

        verify(3, getRequestedFor(urlPathEqualTo("/api/genres/%s".formatted(expectedId))));
    }
}