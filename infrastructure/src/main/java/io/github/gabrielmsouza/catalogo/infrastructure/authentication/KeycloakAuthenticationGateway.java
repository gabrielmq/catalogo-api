package io.github.gabrielmsouza.catalogo.infrastructure.authentication;

import io.github.gabrielmsouza.catalogo.domain.exceptions.InternalErrorException;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.annontations.Keycloak;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.properties.KeycloakProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Component
public class KeycloakAuthenticationGateway implements AuthenticationGateway {
    private final RestClient restClient;
    private final String tokenUri;

    public KeycloakAuthenticationGateway(@Keycloak final RestClient restClient, final KeycloakProperties props) {
        this.restClient = Objects.requireNonNull(restClient);
        this.tokenUri = props.tokenUri();
    }

    @Override
    public AuthenticationResult login(final ClientCredentialsInput input) {
        final var map = new LinkedMultiValueMap<String, String>();
        map.add("client_id", input.clientId());
        map.add("client_secret", input.clientSecret());
        map.add("grant_type", "client_credentials");

        final var result = this.restClient
                .post()
                .uri(this.tokenUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .body(KeycloakAuthenticationResult.class);

        if (result == null) {
            throw InternalErrorException.with("Failed to create client credentials [clientId:%s]".formatted(input.clientId()));
        }

        return new AuthenticationResult(result.accessToken, result.refreshToken);
    }

    @Override
    public AuthenticationResult refresh(final RefreshTokenInput input) {
        final var map = new LinkedMultiValueMap<String, String>();
        map.set("grant_type", "refresh_token");
        map.set("client_id", input.clientId());
        map.set("client_secret", input.clientSecret());
        map.set("refresh_token", input.refreshToken());

        final var result = this.restClient
                .post()
                .uri(this.tokenUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .body(KeycloakAuthenticationResult.class);

        if (result == null) {
            throw InternalErrorException.with("Failed to refresh client credentials [clientId:%s]".formatted(input.clientId()));
        }

        return new AuthenticationResult(result.accessToken, result.refreshToken);
    }

    public record KeycloakAuthenticationResult(
            String accessToken,
            String refreshToken
    ) {
    }
}
