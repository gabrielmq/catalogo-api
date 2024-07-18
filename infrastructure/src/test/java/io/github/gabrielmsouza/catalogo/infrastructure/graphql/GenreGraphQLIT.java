package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.IntegrationTest;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.list.ListGenreUseCase;
import io.github.gabrielmsouza.catalogo.application.genre.save.SaveGenreUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.infrastructure.WebGraphQlSecurityInterceptor;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
public class GenreGraphQLIT {
    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @MockBean
    private SaveGenreUseCase saveGenreUseCase;

    @Autowired
    private WebGraphQlHandler webGraphQlHandler;

    @Autowired
    private WebGraphQlSecurityInterceptor interceptor;

    @Test
    public void givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
        interceptor.setAuthorities();
        final var document = "query genres { genres { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().expect(err -> "Unauthorized".equals(err.getMessage()) && "genres".equals(err.getPath()))
                .verify();
    }

    @Test
    public void givenUserWithAdminRole_whenQueries_shouldReturnResult() {
        interceptor.setAuthorities(Roles.ROLE_ADMIN);

        final var genres = List.of(
                ListGenreUseCase.Output.from(Fixture.Genres.business()),
                ListGenreUseCase.Output.from(Fixture.Genres.tech())
        );

        final var expectedIds = genres.stream().map(ListGenreUseCase.Output::id).toList();

        when(this.listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(0, 10, genres.size(), genres));

        final var document = "query genres { genres { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().verify()
                .path("genres[*].id").entityList(String.class).isEqualTo(expectedIds);
    }

    @Test
    public void givenUserWithSubscriberRole_whenQueries_shouldReturnResult() {
        interceptor.setAuthorities(Roles.ROLE_SUBSCRIBER);

        final var genres = List.of(
                ListGenreUseCase.Output.from(Fixture.Genres.business()),
                ListGenreUseCase.Output.from(Fixture.Genres.tech())
        );

        final var expectedIds = genres.stream().map(ListGenreUseCase.Output::id).toList();

        when(this.listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(0, 10, genres.size(), genres));

        final var document = "query genres { genres { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().verify()
                .path("genres[*].id").entityList(String.class).isEqualTo(expectedIds);
    }

    @Test
    public void givenUserWithGenresRole_whenQueries_shouldReturnResult() {
        interceptor.setAuthorities(Roles.ROLE_GENRES);

        final var genres = List.of(
                ListGenreUseCase.Output.from(Fixture.Genres.business()),
                ListGenreUseCase.Output.from(Fixture.Genres.tech())
        );

        final var expectedIds = genres.stream().map(ListGenreUseCase.Output::id).toList();

        when(this.listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(0, 10, genres.size(), genres));

        final var document = "query genres { genres { id } }";
        final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
        graphQlTesters.document(document).execute()
                .errors().verify()
                .path("genres[*].id").entityList(String.class).isEqualTo(expectedIds);
    }
}
