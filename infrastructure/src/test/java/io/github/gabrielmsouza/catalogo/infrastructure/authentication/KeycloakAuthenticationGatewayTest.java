package io.github.gabrielmsouza.catalogo.infrastructure.authentication;

import io.github.gabrielmsouza.catalogo.AbstractRestClientTest;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.RefreshTokenInput;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.KeycloakAuthenticationGateway.KeycloakAuthenticationResult;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeycloakAuthenticationGatewayTest extends AbstractRestClientTest {
    @Autowired
    private KeycloakAuthenticationGateway gateway;

    @Test
    void givenValidParams_whenCallsLogin_thenShouldReturnClientCredentials() {
        // given
        final var expectedClientId = "client-123";
        final var expectedSecret = "123";
        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";

        final var body =
            Json.writeValueAsString(new KeycloakAuthenticationResult(expectedAccessToken, expectedRefreshToken));

        stubFor(
                post(urlPathEqualTo("/realms/test/protocol/openid-connect/token"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)
        ));

        // when
        final var actualOutput =
                this.gateway.login(new ClientCredentialsInput(expectedClientId, expectedSecret));

        // then
        assertEquals(expectedAccessToken, actualOutput.accessToken());
        assertEquals(expectedRefreshToken, actualOutput.refreshToken());
    }

    @Test
    void givenValidParams_whenCallsRefresh_thenShouldReturnClientCredentials() {
        // given
        final var expectedClientId = "client-123";
        final var expectedSecret = "123";
        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";

        final var body =
                Json.writeValueAsString(new KeycloakAuthenticationResult(expectedAccessToken, expectedRefreshToken));

        stubFor(
                post(urlPathEqualTo("/realms/test/protocol/openid-connect/token"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)
                        ));

        // when
        final var actualOutput =
                this.gateway.refresh(new RefreshTokenInput(expectedClientId, expectedSecret, "old"));

        // then
        assertEquals(expectedAccessToken, actualOutput.accessToken());
        assertEquals(expectedRefreshToken, actualOutput.refreshToken());
    }
}