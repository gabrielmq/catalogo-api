package io.github.gabrielmsouza.catalogo.infrastructure.video;

import io.github.gabrielmsouza.catalogo.application.video.list.ListVideoUseCase;
import io.github.gabrielmsouza.catalogo.infrastructure.video.models.VideoGQL;

import java.time.Instant;

public final class VideoGQLPresenter {
    private VideoGQLPresenter() {}

    public static VideoGQL present(final ListVideoUseCase.Output out) {
        return new VideoGQL(
                out.id(),
                out.title(),
                out.description(),
                out.yearLaunched(),
                out.rating(),
                out.duration(),
                out.opened(),
                out.published(),
                out.video(),
                out.trailer(),
                out.banner(),
                out.thumbnail(),
                out.thumbnailHalf(),
                out.categoriesId(),
                out.castMembersId(),
                out.genresId(),
                formatDate(out.createdAt()),
                formatDate(out.updatedAt())
        );
    }

    private static String formatDate(final Instant date) {
        return date != null ? date.toString() : "";
    }
}
