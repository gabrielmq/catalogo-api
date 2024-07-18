package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.application.genre.list.ListGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.save.SaveGenreUseCase;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.security.Roles;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.GenreGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreGQLInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class GenreGraphQLController {
    private final ListGenreUseCase listGenreUseCase;
    private final SaveGenreUseCase saveGenreUseCase;

    public GenreGraphQLController(
            final ListGenreUseCase listGenreUseCase,
            final SaveGenreUseCase saveGenreUseCase
    ) {
        this.listGenreUseCase = Objects.requireNonNull(listGenreUseCase);
        this.saveGenreUseCase = Objects.requireNonNull(saveGenreUseCase);
    }

    @QueryMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_GENRES })
    public List<GenreGQL> genres(
            @Argument String search,
            @Argument int page,
            @Argument int perPage,
            @Argument String sort,
            @Argument String direction,
            @Argument Set<String> categories
    ) {
        final var aQuery = new ListGenreUseCase.Input(page, perPage, search, sort, direction, categories);
        return this.listGenreUseCase.execute(aQuery)
                .map(GenreGQLPresenter::present)
                .data();
    }

    @MutationMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_GENRES })
    public SaveGenreUseCase.Output saveGenre(@Argument GenreGQLInput input) {
        final var aInput = new SaveGenreUseCase.Input(
                input.id(),
                input.name(),
                input.active(),
                input.categories(),
                input.createdAt(),
                input.updatedAt(),
                input.deletedAt()
        );
        return this.saveGenreUseCase.execute(aInput);
    }
}
