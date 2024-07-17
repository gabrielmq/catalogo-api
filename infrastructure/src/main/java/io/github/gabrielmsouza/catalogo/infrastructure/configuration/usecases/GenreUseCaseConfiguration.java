package io.github.gabrielmsouza.catalogo.infrastructure.configuration.usecases;

import io.github.gabrielmsouza.catalogo.application.genre.delete.DeleteGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.get.GetAllGenresByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.list.ListGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.save.SaveGenreUseCase;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
public class GenreUseCaseConfiguration {
    private final GenreGateway gateway;

    public GenreUseCaseConfiguration(final GenreGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Bean
    SaveGenreUseCase saveGenreUseCase() {
        return new SaveGenreUseCase(gateway);
    }

    @Bean
    DeleteGenreUseCase deleteGenreUseCase() {
        return new DeleteGenreUseCase(gateway);
    }

    @Bean
    ListGenreUseCase listGenreUseCase() {
        return new ListGenreUseCase(gateway);
    }

    @Bean
    GetAllGenresByIdUseCase getAllGenresByIdUseCase() {
        return new GetAllGenresByIdUseCase(gateway);
    }
}
