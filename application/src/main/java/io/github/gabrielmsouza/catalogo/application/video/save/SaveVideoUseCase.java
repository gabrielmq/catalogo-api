package io.github.gabrielmsouza.catalogo.application.video.save;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.exceptions.DomainException;
import io.github.gabrielmsouza.catalogo.domain.validation.Error;
import io.github.gabrielmsouza.catalogo.domain.video.Video;
import io.github.gabrielmsouza.catalogo.domain.video.VideoGateway;

import java.util.Objects;
import java.util.Set;

public class SaveVideoUseCase extends UseCase<SaveVideoUseCase.Input, SaveVideoUseCase.Output> {
    private final VideoGateway videoGateway;

    public SaveVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public Output execute(final Input input) {
        if (Objects.isNull(input)) {
            throw DomainException.with(Error.with("'SaveVideoUseCase.Input' cannot be null"));
        }

        final var video = this.videoGateway.save(Video.with(
                input.id(),
                input.title(),
                input.description(),
                input.launchedAt(),
                input.duration(),
                input.rating(),
                input.opened(),
                input.published(),
                input.createdAt(),
                input.updatedAt(),
                input.video(),
                input.trailer(),
                input.banner(),
                input.thumbnail(),
                input.thumbnailHalf(),
                input.categories(),
                input.castMembers(),
                input.genres()
        ));

        return new Output(video.id());
    }

    public record Input(
            String id,
            String title,
            String description,
            Integer launchedAt,
            double duration,
            String rating,
            boolean opened,
            boolean published,
            String createdAt,
            String updatedAt,
            String video,
            String trailer,
            String banner,
            String thumbnail,
            String thumbnailHalf,
            Set<String> categories,
            Set<String> castMembers,
            Set<String> genres
    ) {
    }

    public record Output(String id) {}
}
