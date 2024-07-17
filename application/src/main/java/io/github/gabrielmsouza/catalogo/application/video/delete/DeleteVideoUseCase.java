package io.github.gabrielmsouza.catalogo.application.video.delete;

import io.github.gabrielmsouza.catalogo.application.UnitUseCase;
import io.github.gabrielmsouza.catalogo.domain.video.VideoGateway;

import java.util.Objects;

public class DeleteVideoUseCase extends UnitUseCase<DeleteVideoUseCase.Input> {
    private final VideoGateway videoGateway;

    public DeleteVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final Input input) {
        if (Objects.isNull(input) || Objects.isNull(input.videoId())) {
            return;
        }
        this.videoGateway.deleteById(input.videoId());
    }

    public record Input(String videoId) {}
}
