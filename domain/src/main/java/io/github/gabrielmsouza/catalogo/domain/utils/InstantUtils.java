package io.github.gabrielmsouza.catalogo.domain.utils;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;

public final class InstantUtils {

    private InstantUtils() {}

    public static Instant now() {
        return Instant.now().truncatedTo(MILLIS);
    }
}
