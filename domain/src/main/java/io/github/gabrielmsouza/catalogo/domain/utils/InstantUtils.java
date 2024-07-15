package io.github.gabrielmsouza.catalogo.domain.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;

public final class InstantUtils {
    public static final int UNIX_PRECISION = 1_000;

    private InstantUtils() {}

    public static Instant now() {
        return Instant.now().truncatedTo(MILLIS);
    }

    public static Instant fromTimestamp(final Long timestamp) {
        return Optional.ofNullable(timestamp)
                .map(time -> new Timestamp(time / UNIX_PRECISION).toInstant())
                .orElse(null);
    }
}
