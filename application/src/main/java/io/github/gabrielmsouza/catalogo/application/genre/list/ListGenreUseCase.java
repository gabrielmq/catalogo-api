package io.github.gabrielmsouza.catalogo.application.genre.list;

import io.github.gabrielmsouza.catalogo.application.UseCase;
import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class ListGenreUseCase extends UseCase<ListGenreUseCase.Input, Pagination<ListGenreUseCase.Output>> {
    private final GenreGateway gateway;

    public ListGenreUseCase(final GenreGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public Pagination<Output> execute(final Input input) {
        return this.gateway.findAll(input.toQuery()).map(Output::from);
    }

    public record Input(
            int page,
            int perPage,
            String terms,
            String sort,
            String direction,
            Set<String> categories
    ) {
        public GenreSearchQuery toQuery() {
            return new GenreSearchQuery(
                    page(),
                    perPage(),
                    terms(),
                    sort(),
                    direction(),
                    categories()
            );
        }
    }

    public record Output(
            String id,
            String name,
            boolean active,
            Set<String> categories,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        public static Output from(final Genre genre) {
            return new Output(
                    genre.id(),
                    genre.name(),
                    genre.active(),
                    genre.categories(),
                    genre.createdAt(),
                    genre.updatedAt(),
                    genre.deletedAt()
            );
        }
    }
}
