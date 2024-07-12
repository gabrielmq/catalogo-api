package io.github.gabrielmsouza.catalogo.application.castmember.delete;

import io.github.gabrielmsouza.catalogo.application.UnitUseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;

import java.util.Objects;

public class DeleteCastMemberUseCase extends UnitUseCase<String> {
    private final CastMemberGateway gateway;

    public DeleteCastMemberUseCase(final CastMemberGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public void execute(final String anIn) {
        if (Objects.nonNull(anIn)) {
            this.gateway.deleteById(anIn);
        }
    }
}
