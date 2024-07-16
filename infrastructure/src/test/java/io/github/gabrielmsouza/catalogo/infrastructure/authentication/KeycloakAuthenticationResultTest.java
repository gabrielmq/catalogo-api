package io.github.gabrielmsouza.catalogo.infrastructure.authentication;

import io.github.gabrielmsouza.catalogo.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class KeycloakAuthenticationResultTest {
    @Autowired
    private JacksonTester<KeycloakAuthenticationGateway.KeycloakAuthenticationResult> json;

    @Test
    public void testUnmarshall_shouldReadSnakeCaseResponse() throws IOException {
        final var keycloakResponse = """
                {
                    "access_token": "a26ce442a369459f9a1579abe6727efc",
                    "refresh_token": "io1ji3o21jpi3o1jpi3j1i2j312j312jp"
                }
                """;

        final var actualCategory = this.json.parse(keycloakResponse);

        assertThat(actualCategory)
                .hasFieldOrPropertyWithValue("accessToken", "a26ce442a369459f9a1579abe6727efc")
                .hasFieldOrPropertyWithValue("refreshToken", "io1ji3o21jpi3o1jpi3j1i2j312j312jp");
    }
}
