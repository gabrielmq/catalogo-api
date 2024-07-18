package io.github.gabrielmsouza.catalogo.infrastructure.configuration.usecases;

import io.github.gabrielmsouza.catalogo.application.video.delete.DeleteVideoUseCase;
import io.github.gabrielmsouza.catalogo.application.video.get.GetVideoUseCase;
import io.github.gabrielmsouza.catalogo.application.video.list.ListVideoUseCase;
import io.github.gabrielmsouza.catalogo.application.video.save.SaveVideoUseCase;
import io.github.gabrielmsouza.catalogo.domain.video.VideoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
public class VideoUseCaseConfiguration {
    private final VideoGateway videoGateway;

    public VideoUseCaseConfiguration(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Bean
    DeleteVideoUseCase deleteVideoUseCase() {
        return new DeleteVideoUseCase(videoGateway);
    }

    @Bean
    ListVideoUseCase listVideoUseCase() {
        return new ListVideoUseCase(videoGateway);
    }

    @Bean
    SaveVideoUseCase saveVideoUseCase() {
        return new SaveVideoUseCase(videoGateway);
    }

    @Bean
    GetVideoUseCase getVideoUseCase() {
        return new GetVideoUseCase(videoGateway);
    }
}
