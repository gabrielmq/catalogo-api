package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.category.get.GetAllCategoriesByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.get.GetAllGenresByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.video.list.ListVideoUseCase;
import io.github.gabrielmsouza.catalogo.application.video.save.SaveVideoUseCase;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.CastMemberGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.models.CastMemberGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.category.CategoryGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.category.models.CategoryGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.security.Roles;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.GenreGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.models.GenreGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.video.VideoGQLPresenter;
import io.github.gabrielmsouza.catalogo.infrastructure.video.models.VideoGQL;
import io.github.gabrielmsouza.catalogo.infrastructure.video.models.VideoGQLInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class VideoGraphQLController {
    private final ListVideoUseCase listVideoUseCase;
    private final GetAllCastMembersByIdUseCase getAllCastMembersByIdUseCase;
    private final GetAllCategoriesByIdUseCase getAllCategoriesByIdUseCase;
    private final GetAllGenresByIdUseCase getAllGenresByIdUseCase;
    private final SaveVideoUseCase saveVideoUseCase;

    public VideoGraphQLController(
            final ListVideoUseCase listVideoUseCase,
            final GetAllCastMembersByIdUseCase getAllCastMembersByIdUseCase,
            final GetAllCategoriesByIdUseCase getAllCategoriesByIdUseCase,
            final GetAllGenresByIdUseCase getAllGenresByIdUseCase,
            final SaveVideoUseCase saveVideoUseCase
    ) {
        this.listVideoUseCase = Objects.requireNonNull(listVideoUseCase);
        this.getAllCastMembersByIdUseCase = Objects.requireNonNull(getAllCastMembersByIdUseCase);
        this.getAllCategoriesByIdUseCase = Objects.requireNonNull(getAllCategoriesByIdUseCase);
        this.getAllGenresByIdUseCase = Objects.requireNonNull(getAllGenresByIdUseCase);
        this.saveVideoUseCase = Objects.requireNonNull(saveVideoUseCase);
    }

    @QueryMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_VIDEOS })
    public List<VideoGQL> videos(
            @Argument final String search,
            @Argument final int page,
            @Argument final int perPage,
            @Argument final String sort,
            @Argument final String direction,
            @Argument final String rating,
            @Argument final Integer yearLaunched,
            @Argument final Set<String> castMembers,
            @Argument final Set<String> categories,
            @Argument final Set<String> genres
    ) {
        final var input = new ListVideoUseCase.Input(
                page,
                perPage,
                search,
                sort,
                direction,
                rating,
                yearLaunched,
                categories,
                castMembers,
                genres
        );
        return this.listVideoUseCase.execute(input)
                .map(VideoGQLPresenter::present)
                .data();
    }

    @SchemaMapping(typeName = "Video", field = "castMembers")
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_VIDEOS })
    public List<CastMemberGQL> castMembers(final VideoGQL video) {
        final var input = new GetAllCastMembersByIdUseCase.Input(video.castMembersId());
        return this.getAllCastMembersByIdUseCase.execute(input).stream()
                .map(CastMemberGQLPresenter::present)
                .toList();
    }

    @SchemaMapping(typeName = "Video", field = "genres")
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_VIDEOS })
    public List<GenreGQL> genres(final VideoGQL video) {
        final var input = new GetAllGenresByIdUseCase.Input(video.genresId());
        return this.getAllGenresByIdUseCase.execute(input).stream()
                .map(GenreGQLPresenter::present)
                .toList();
    }

    @SchemaMapping(typeName = "Video", field = "categories")
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_VIDEOS })
    public List<CategoryGQL> categories(final VideoGQL video) {
        final var input = new GetAllCategoriesByIdUseCase.Input(video.categoriesId());
        return this.getAllCategoriesByIdUseCase.execute(input).stream()
                .map(CategoryGQLPresenter::present)
                .toList();
    }

    @MutationMapping
    @Secured({ Roles.ROLE_ADMIN, Roles.ROLE_SUBSCRIBER, Roles.ROLE_VIDEOS })
    public SaveVideoUseCase.Output saveVideo(@Argument(name = "input") final VideoGQLInput arg) {
        final var input = new SaveVideoUseCase.Input(
                arg.id(), arg.title(), arg.description(), arg.yearLaunched(), arg.duration(),
                arg.rating(), arg.opened(), arg.published(), arg.createdAt(), arg.updatedAt(),
                arg.video(), arg.trailer(), arg.banner(), arg.thumbnail(), arg.thumbnailHalf(),
                arg.categoriesId(), arg.castMembersId(), arg.genresId()
        );
        return this.saveVideoUseCase.execute(input);
    }
}
