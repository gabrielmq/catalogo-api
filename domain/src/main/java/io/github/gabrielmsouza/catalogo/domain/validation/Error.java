package io.github.gabrielmsouza.catalogo.domain.validation;

public record Error(String message) {
    public static Error with(final String aMessage) {
        return new Error(aMessage);
    }
}
