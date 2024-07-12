package io.github.gabrielmsouza.catalogo.infrastructure.job;

import io.github.gabrielmsouza.catalogo.infrastructure.authentication.RefreshClientCredentials;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class ClientCredentialsJob {

    private final RefreshClientCredentials clientCredentials;

    public ClientCredentialsJob(final RefreshClientCredentials clientCredentials) {
        this.clientCredentials = Objects.requireNonNull(clientCredentials);
    }

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES, initialDelay = 3)
    public void refreshCredentials() {
        this.clientCredentials.refresh();
    }
}
