package io.github.gabrielmsouza.catalogo.infrastructure.authentication;

import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.RefreshTokenInput;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.properties.KeycloakProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@Component
public class ClientCredentialsManager implements GetClientCredentials, RefreshClientCredentials {
    // AtomicReferenceFieldUpdater is a class that allows to update the value of a field atomically.
    private static final AtomicReferenceFieldUpdater<ClientCredentialsManager, ClientCredentials> UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(ClientCredentialsManager.class, ClientCredentials.class, "credentials");

    // volatile indicates that the value of the field will never be cached thread-locally.
    // All reads will go straight to "main memory";
    private volatile ClientCredentials credentials;

    private final AuthenticationGateway authenticationGateway;
    private final KeycloakProperties keycloakProperties;

    public ClientCredentialsManager(
            final AuthenticationGateway authenticationGateway,
            final KeycloakProperties keycloakProperties
    ) {
        this.authenticationGateway = Objects.requireNonNull(authenticationGateway);
        this.keycloakProperties = Objects.requireNonNull(keycloakProperties);
    }

    @Override
    public String retrieve() {
        return this.credentials.accessToken();
    }

    @Override
    public void refresh() {
        final var result = Objects.isNull(this.credentials) ? login() : refreshToken();
        UPDATER.set(this, new ClientCredentials(clientId(), result.accessToken(), result.refreshToken()));
    }

    private AuthenticationGateway.AuthenticationResult login() {
        return this.authenticationGateway.login(new ClientCredentialsInput(clientId(), clientSecret()));
    }

    private AuthenticationGateway.AuthenticationResult refreshToken() {
        try {
            return this.authenticationGateway.refresh(new RefreshTokenInput(clientId(), clientSecret(), this.credentials.refreshToken()));
        } catch (RuntimeException ex) {
            return this.login();
        }
    }

    private String clientId() {
        return this.keycloakProperties.clientId();
    }

    private String clientSecret() {
        return this.keycloakProperties.clientSecret();
    }

    record ClientCredentials(String clientId, String accessToken, String refreshToken) {
    }
}
