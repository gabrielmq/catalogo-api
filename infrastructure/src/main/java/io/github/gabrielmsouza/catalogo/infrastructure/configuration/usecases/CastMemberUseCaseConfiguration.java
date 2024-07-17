package io.github.gabrielmsouza.catalogo.infrastructure.configuration.usecases;

import io.github.gabrielmsouza.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.list.ListCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.application.castmember.save.SaveCastMemberUseCase;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
public class CastMemberUseCaseConfiguration {
    private final CastMemberGateway gateway;

    public CastMemberUseCaseConfiguration(final CastMemberGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Bean
    SaveCastMemberUseCase saveCastMemberUseCase() {
        return new SaveCastMemberUseCase(gateway);
    }

    @Bean
    DeleteCastMemberUseCase deleteCastMemberUseCase() {
        return new DeleteCastMemberUseCase(gateway);
    }

    @Bean
    ListCastMemberUseCase listCastMemberUseCase() {
        return new ListCastMemberUseCase(gateway);
    }

    @Bean
    GetAllCastMembersByIdUseCase getAllCastMembersByIdUseCase() {
        return new GetAllCastMembersByIdUseCase(gateway);
    }
}
