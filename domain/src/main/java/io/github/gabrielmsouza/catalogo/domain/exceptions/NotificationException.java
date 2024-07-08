package io.github.gabrielmsouza.catalogo.domain.exceptions;

import io.github.gabrielmsouza.catalogo.domain.validation.handler.Notification;

public class NotificationException extends DomainException {
    public NotificationException(final String aMessage, final Notification aNotification) {
        super(aMessage, aNotification.getErrors());
    }

    public static NotificationException with(final String aMessage, final Notification aNotification) {
        return new NotificationException(aMessage, aNotification);
    }
}
