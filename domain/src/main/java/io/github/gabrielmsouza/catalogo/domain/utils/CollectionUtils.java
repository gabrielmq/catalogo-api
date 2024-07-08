package io.github.gabrielmsouza.catalogo.domain.utils;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static <IN, OUT> Set<OUT> mapTo(final Set<IN> list, final Function<IN, OUT> mapper) {
        return Objects.nonNull(list) ? list.stream().map(mapper).collect(Collectors.toSet()) : null;
    }

    public static <T> Set<T> nullIfEmpty(final Set<T> values) {
        return Objects.isNull(values) || values.isEmpty() ? null : values;
    }
}
