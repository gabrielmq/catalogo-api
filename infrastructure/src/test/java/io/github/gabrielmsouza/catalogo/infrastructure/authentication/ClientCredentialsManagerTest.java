package io.github.gabrielmsouza.catalogo.infrastructure.authentication;

import io.github.gabrielmsouza.catalogo.domain.exceptions.InternalErrorException;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.AuthenticationResult;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.AuthenticationGateway.RefreshTokenInput;
import io.github.gabrielmsouza.catalogo.infrastructure.authentication.ClientCredentialsManager.ClientCredentials;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.properties.KeycloakProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ClientCredentialsManagerTest {
    @Mock
    private KeycloakProperties keycloakProperties;

    @Mock
    private AuthenticationGateway authenticationGateway;

    @InjectMocks
    private ClientCredentialsManager manager;

    @Test
    public void givenValidAuthenticationResult_whenCallsRefresh_thenShouldCreateCredentials() {
        // given
        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";
        final var expectedClientId = "client-id";
        final var expectedClientSecret = "sad1324213";

        doReturn(expectedClientId).when(keycloakProperties).clientId();
        doReturn(expectedClientSecret).when(keycloakProperties).clientSecret();

        doReturn(new AuthenticationResult(expectedAccessToken, expectedRefreshToken))
                .when(authenticationGateway)
                .login(new ClientCredentialsInput(expectedClientId, expectedClientSecret));

        // when
        this.manager.refresh();
        final var actualToken = this.manager.retrieve();

        // then
        assertEquals(expectedAccessToken, actualToken);
    }

    @Test
    public void givenPreviousAuthentication_whenCallsRefresh_thenShouldUpdateCredentials() {
        // given
        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";
        final var expectedClientId = "client-id";
        final var expectedClientSecret = "sad1324213";

        ReflectionTestUtils.setField(this.manager, "credentials", new ClientCredentials(expectedClientId, "acc", "ref"));

        doReturn(expectedClientId).when(keycloakProperties).clientId();
        doReturn(expectedClientSecret).when(keycloakProperties).clientSecret();

        doReturn(new AuthenticationResult(expectedAccessToken, expectedRefreshToken))
                .when(authenticationGateway)
                .refresh(new RefreshTokenInput(expectedClientId, expectedClientSecret, "ref"));

        // when
        this.manager.refresh();

        final var actualCredentials = (ClientCredentials) ReflectionTestUtils.getField(this.manager, "credentials");

        // then
        assertEquals(expectedAccessToken, actualCredentials.accessToken());
        assertEquals(expectedRefreshToken, actualCredentials.refreshToken());
    }

    @Test
    public void givenErrorFromRefreshToken_whenCallsRefresh_thenShouldFallbackToLogin() {
        // given
        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";
        final var expectedClientId = "client-id";
        final var expectedClientSecret = "sad1324213";

        ReflectionTestUtils.setField(this.manager, "credentials", new ClientCredentials(expectedClientId, "acc", "ref"));

        doReturn(expectedClientId).when(keycloakProperties).clientId();
        doReturn(expectedClientSecret).when(keycloakProperties).clientSecret();

        doThrow(InternalErrorException.with("BLA!"))
                .when(authenticationGateway)
                .refresh(new RefreshTokenInput(expectedClientId, expectedClientSecret, "ref"));

        doReturn(new AuthenticationResult(expectedAccessToken, expectedRefreshToken))
                .when(authenticationGateway)
                .login(new ClientCredentialsInput(expectedClientId, expectedClientSecret));

        // when
        this.manager.refresh();

        final var actualCredentials = (ClientCredentials) ReflectionTestUtils.getField(this.manager, "credentials");

        // then
        assertEquals(expectedAccessToken, actualCredentials.accessToken());
        assertEquals(expectedRefreshToken, actualCredentials.refreshToken());
    }
}