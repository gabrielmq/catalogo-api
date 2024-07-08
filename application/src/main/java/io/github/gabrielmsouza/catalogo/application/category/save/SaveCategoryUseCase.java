package io.github.gabrielmsouza.catalogo.application.category.save;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.domain.exceptions.NotificationException;
import io.github.gabrielmsouza.catalogo.domain.validation.Error;
import io.github.gabrielmsouza.catalogo.domain.validation.handler.Notification;

import java.util.Objects;

public class SaveCategoryUseCase extends UseCase<Category, Category> {
    private final CategoryGateway gateway;

    public SaveCategoryUseCase(final CategoryGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    public Category execute(final Category aCategory) {
        if (Objects.isNull(aCategory)) {
            throw NotificationException.with(Error.with("'aCategory' cannot be null"));
        }

        final var notification = Notification.create();
        aCategory.validate(notification);
        if (notification.hasErrors()) {
            throw NotificationException.with("Invalid category", notification);
        }
        return this.gateway.save(aCategory);
    }
}
