package io.github.gabrielmsouza.catalogo.infrastructure.exceptions;

import io.github.gabrielmsouza.catalogo.domain.exceptions.InternalErrorException;

public class NotFoundException extends InternalErrorException {
    protected NotFoundException(final String aMessage, final Throwable aCause) {
        super(aMessage, aCause);
    }

    public static NotFoundException with(final String aMessage) {
        return new NotFoundException(aMessage, null);
    }
}
