package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.AbstractRestClientTest;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.exceptions.InternalErrorException;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.ClientCredentialsManager;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryDTO;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class CategoryRestClientTest extends AbstractRestClientTest {
    @Autowired
    private CategoryRestClient restClient;

    @SpyBean
    private ClientCredentialsManager credentialsManager;

    @Test
    void givenACategory_whenReceive200FromServer_thenShouldBeOk() {
        // given
        final var aulas = Fixture.Categories.aulas();
        final var response = new CategoryDTO(
                aulas.id(),
                aulas.name(),
                aulas.description(),
                aulas.active(),
                aulas.createdAt(),
                aulas.updatedAt(),
                aulas.deletedAt()
        );

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(aulas.id())))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(Json.writeValueAsString(response))
                        )
        );

        // when
        final var actualCategory = this.restClient.categoryOfId(aulas.id()).get();

        // then
        assertEquals(aulas.id(), actualCategory.id());
        assertEquals(aulas.name(), actualCategory.name());
        assertEquals(aulas.description(), actualCategory.description());
        assertEquals(aulas.active(), actualCategory.active());
        assertEquals(aulas.createdAt(), actualCategory.createdAt());
        assertEquals(aulas.updatedAt(), actualCategory.updatedAt());
        assertEquals(aulas.deletedAt(), actualCategory.deletedAt());

        verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(aulas.id()))));
    }

    @Test
    void givenACategory_whenReceiveTwoCalls_thenShouldReturnCachedValue() {
        // given
        final var aulas = Fixture.Categories.aulas();
        final var response = new CategoryDTO(
                aulas.id(),
                aulas.name(),
                aulas.description(),
                aulas.active(),
                aulas.createdAt(),
                aulas.updatedAt(),
                aulas.deletedAt()
        );

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(aulas.id())))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(Json.writeValueAsString(response))
                        )
        );

        // when
        this.restClient.categoryOfId(aulas.id()).get();
        final var actualCategory = this.restClient.categoryOfId(aulas.id()).get();

        // then
        assertEquals(aulas.id(), actualCategory.id());
        assertEquals(aulas.name(), actualCategory.name());
        assertEquals(aulas.description(), actualCategory.description());
        assertEquals(aulas.active(), actualCategory.active());
        assertEquals(aulas.createdAt(), actualCategory.createdAt());
        assertEquals(aulas.updatedAt(), actualCategory.updatedAt());
        assertEquals(aulas.deletedAt(), actualCategory.deletedAt());

        final var actualCachedValue = cache("admin-categories").get(aulas.id());
        assertEquals(actualCategory, actualCachedValue.get());

        verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(aulas.id()))));
    }

    @Test
    void givenACategory_whenReceive404FromServer_thenShouldReturnInternalError() {
        // given
        final var expectedId = "123";

        final var response = Json.writeValueAsString(Map.of("message", "Not Found"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
                        .willReturn(
                                aResponse()
                                        .withStatus(404)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(response)
                        )
        );

        // when
        final var actualCategory = this.restClient.categoryOfId(expectedId);

        // then
        assertTrue(actualCategory.isEmpty());

        verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }

    @Test
    void givenACategory_whenReceive5xxFromServer_thenShouldReturnEmpty() {
        // given
        final var expectedId = "123";
        final var expectedErrorMessage = "Error observed from categories [resourceId:%s] [status:500]".formatted(expectedId);

        final var response = Json.writeValueAsString(Map.of("message", "Internal Server Error"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(response)
                        )
        );

        // when
        final var actualException =
                assertThrows(InternalErrorException.class, () -> this.restClient.categoryOfId(expectedId));

        // then
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(2, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }

    @Test
    void givenACategory_whenReceiveTimeoutFromServer_thenShouldReturnInternalError() {
        // given
        final var expectedId = "123";
        final var expectedErrorMessage = "Timeout observed from categories [resourceId:%s]".formatted(expectedId);

        final var response = Json.writeValueAsString(Map.of("message", "Internal Server Error"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withFixedDelay(6000)
                                        .withBody(response)
                        )
        );

        // when
        final var actualException =
                assertThrows(InternalErrorException.class, () -> this.restClient.categoryOfId(expectedId));

        // then
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(2, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }

    @Test
    void givenACategory_whenBulkheadIsFull_thenShouldReturnError() {
        // given
        final var expectedErrorMessage = "Bulkhead 'categories' is full and does not permit further calls";
        acquireBulkheadPermission(CategoryRestClient.NAMESPACE);

        // when
        final var actualException =
                assertThrows(BulkheadFullException.class, () -> this.restClient.categoryOfId("123"));

        // then
        assertEquals(expectedErrorMessage, actualException.getMessage());

        releaseBulkheadPermission(CategoryRestClient.NAMESPACE);
    }

    @Test
    public void givenCall_whenCBIsOpen_shouldReturnError() {
        // given
        transitionToOpenState(CategoryRestClient.NAMESPACE);
        final var expectedId = "123";
        final var expectedErrorMessage = "CircuitBreaker 'categories' is OPEN and does not permit further calls";

        // when
        final var actualException = assertThrows(CallNotPermittedException.class, () -> this.restClient.categoryOfId(expectedId));

        // then
        checkCircuitBreakerState(CategoryRestClient.NAMESPACE, CircuitBreaker.State.OPEN);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(0, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }

    @Test
    void givenServerError_whenIsMoreThanThreshold_thenShouldOpenCircuitBreaker() {
        // given
        final var expectedId = "123";
        final var expectedErrorMessage = "CircuitBreaker 'categories' is OPEN and does not permit further calls";

        final var response = Json.writeValueAsString(Map.of("message", "Internal Server Error"));

        final var expectedToken = "access-123";
        doReturn(expectedToken).when(credentialsManager).retrieve();

        stubFor(
            get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(response)));

        // when
        assertThrows(InternalErrorException.class, () -> this.restClient.categoryOfId(expectedId));
        final var actualException = assertThrows(CallNotPermittedException.class, () -> this.restClient.categoryOfId(expectedId));

        // then
        checkCircuitBreakerState(CategoryRestClient.NAMESPACE, CircuitBreaker.State.OPEN);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(3, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }
}