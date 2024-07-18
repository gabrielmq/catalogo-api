package io.github.gabrielmsouza.catalogo.application.video.list;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.domain.video.Video;
import io.github.gabrielmsouza.catalogo.domain.video.VideoGateway;
import io.github.gabrielmsouza.catalogo.domain.video.VideoSearchQuery;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class ListVideoUseCase extends UseCase<ListVideoUseCase.Input, Pagination<ListVideoUseCase.Output>> {
    private final VideoGateway videoGateway;

    public ListVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public Pagination<Output> execute(final ListVideoUseCase.Input input) {
        final var aQuery = new VideoSearchQuery(
                input.page(),
                input.perPage(),
                input.terms(),
                input.sort(),
                input.direction(),
                input.rating(),
                input.launchedAt(),
                input.categories(),
                input.castMembers(),
                input.genres()
        );

        return this.videoGateway.findAll(aQuery)
                .map(Output::from);
    }

    public record Input(
            int page,
            int perPage,
            String terms,
            String sort,
            String direction,
            String rating,
            Integer launchedAt,
            Set<String> categories,
            Set<String> castMembers,
            Set<String> genres
    ) {

    }

    public record Output(
            String id,
            String title,
            String description,
            int yearLaunched,
            String rating,
            Double duration,
            boolean opened,
            boolean published,
            String video,
            String trailer,
            String banner,
            String thumbnail,
            String thumbnailHalf,
            Set<String> categoriesId,
            Set<String> castMembersId,
            Set<String> genresId,
            Instant createdAt,
            Instant updatedAt
    ) {

        public static Output from(final Video video) {
            return new Output(
                    video.id(),
                    video.title(),
                    video.description(),
                    video.launchedAt().getValue(),
                    video.rating().getName(),
                    video.duration(),
                    video.opened(),
                    video.published(),
                    video.video(),
                    video.trailer(),
                    video.banner(),
                    video.thumbnail(),
                    video.thumbnailHalf(),
                    video.categories(),
                    video.castMembers(),
                    video.genres(),
                    video.createdAt(),
                    video.updatedAt()
            );
        }
    }
}
