package io.github.gabrielmsouza.catalogo.domain.validation;

import java.util.List;
import java.util.Objects;

public interface ValidationHandler {
    ValidationHandler append(Error anError);
    ValidationHandler append(ValidationHandler anHandler);
    <T> T validate(Validation<T> aValidation);
    List<Error> getErrors();

    default boolean hasErrors() {
        return Objects.nonNull(getErrors()) && !getErrors().isEmpty();
    }

    default Error getFirstError() {
        if (Objects.nonNull(getErrors()) && !getErrors().isEmpty()) {
            return getErrors().get(0);
        }
        return null;
    }

    @FunctionalInterface
    interface Validation<T> {
        T validate();
    }
}
