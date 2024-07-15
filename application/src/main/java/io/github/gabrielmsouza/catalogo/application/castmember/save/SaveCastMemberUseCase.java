package io.github.gabrielmsouza.catalogo.application.castmember.save;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.domain.exceptions.NotificationException;
import io.github.gabrielmsouza.catalogo.domain.validation.Error;
import io.github.gabrielmsouza.catalogo.domain.validation.handler.Notification;

import java.util.Objects;

public class SaveCastMemberUseCase extends UseCase<CastMember, CastMember> {
    private final CastMemberGateway gateway;

    public SaveCastMemberUseCase(final CastMemberGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public CastMember execute(final CastMember aCastMember) {
        if (aCastMember == null) {
            throw NotificationException.with(Error.with("'aCastMember' cannot be null"));
        }

        final var notification = Notification.create();
        aCastMember.validate(notification);
        if (notification.hasErrors()) {
            throw NotificationException.with("Invalid cast member", notification);
        }
        return this.gateway.save(aCastMember);
    }
}
