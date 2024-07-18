package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.application.genre.get.GetAllGenresByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.list.ListGenreUseCase;
import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreGQL;

import java.time.Instant;

public final class GenreGQLPresenter {
    private GenreGQLPresenter() {}

    public static GenreGQL present(final ListGenreUseCase.Output out) {
        return new GenreGQL(
                out.id(),
                out.name(),
                out.active(),
                out.categories(),
                formatDate(out.createdAt()),
                formatDate(out.updatedAt()),
                formatDate(out.deletedAt())
        );
    }

    public static GenreGQL present(final GetAllGenresByIdUseCase.Output out) {
        return new GenreGQL(
                out.id(),
                out.name(),
                out.active(),
                out.categories(),
                formatDate(out.createdAt()),
                formatDate(out.updatedAt()),
                formatDate(out.deletedAt())
        );
    }

    private static String formatDate(final Instant date) {
        return date != null ? date.toString() : "";
    }
}
