package io.github.gabrielmsouza.catalogo.infrastructure.graphql;

import io.github.gabrielmsouza.catalogo.GraphQLControllerTest;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.Fixture;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQLControllerTest(controllers = CategoryGraphQLController.class)
public class CategoryGraphQLControllerTest {
    @MockBean
    private ListCategoryUseCase listCategoryUseCase;

    @MockBean
    private SaveCategoryUseCase saveCategoryUseCase;

    @Autowired
    private GraphQlTester graphql;

    @Test
    void givenDefaultArguments_whenCallsListCategories_thenShouldReturn() {
        // given
        final var expectedCategories = List.of(
                ListCategoryUseCase.Output.from(Fixture.Categories.lives()),
                ListCategoryUseCase.Output.from(Fixture.Categories.aulas())
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedSearch = "";

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedCategories.size(),
                expectedCategories
        );

        when(this.listCategoryUseCase.execute(any())).thenReturn(pagination);

        final var query = """
                    {
                        categories {
                            id
                            name
                        }
                    }
                """;

        // when
        final var actualCategory = this.graphql.document(query).execute()
                .path("categories")
                .entityList(ListCategoryUseCase.Output.class)
                .get();

        // then
        assertTrue(
                actualCategory.size() == expectedCategories.size()
                        && actualCategory.containsAll(expectedCategories)
        );

        final var captor = ArgumentCaptor.forClass(CategorySearchQuery.class);
        verify(this.listCategoryUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSearch, actualQuery.terms());
    }

    @Test
    void givenCustomArguments_whenCallsListCategories_thenShouldReturn() {
        // given
        final var expectedCategories = List.of(
                ListCategoryUseCase.Output.from(Fixture.Categories.lives()),
                ListCategoryUseCase.Output.from(Fixture.Categories.aulas())
        );

        final var expectedPage = 2;
        final var expectedPerPage = 15;
        final var expectedSort = "id";
        final var expectedDirection = "desc";
        final var expectedSearch = "asd";

        final var pagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedCategories.size(),
                expectedCategories
        );

        when(this.listCategoryUseCase.execute(any())).thenReturn(pagination);

        final var query = """
                    query AllCategories($search: String, $page: Int, $perPage: Int, $sort: String, $direction: String) {
                        categories(search: $search, page: $page, perPage: $perPage, sort: $sort, direction: $direction) {
                            id
                            name
                        }
                    }
                """;

        // when
        final var actualCategory = this.graphql.document(query)
                .variable("search", expectedSearch)
                .variable("page", expectedPage)
                .variable("perPage", expectedPerPage)
                .variable("sort", expectedSort)
                .variable("direction", expectedDirection)
                .execute()
                .path("categories")
                .entityList(ListCategoryUseCase.Output.class)
                .get();

        // then
        assertTrue(
                actualCategory.size() == expectedCategories.size()
                        && actualCategory.containsAll(expectedCategories)
        );

        final var captor = ArgumentCaptor.forClass(CategorySearchQuery.class);
        verify(this.listCategoryUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSearch, actualQuery.terms());
    }

    @Test
    public void givenCategoryInput_whenCallsSaveCategoryMutation_thenShouldPersistAndReturn() {
        // given
        final var expectedId = IDUtils.uuid();
        final var expectedName = "Aulas";
        final var expectedDescription = "A melhor categoria";
        final var expectedActive = false;
        final var expectedCreatedAt = InstantUtils.now();
        final var expectedUpdatedAt = InstantUtils.now();
        final var expectedDeletedAt = InstantUtils.now();

        final var input = Map.of(
                "id", expectedId,
                "name", expectedName,
                "description", expectedDescription,
                "active", expectedActive,
                "createdAt", expectedCreatedAt.toString(),
                "updatedAt", expectedUpdatedAt.toString(),
                "deletedAt", expectedDeletedAt.toString()
        );

        final var query = """
                mutation SaveCategory($input: CategoryInput!) {
                    category: saveCategory(input: $input) {
                        id
                        name
                        description
                    }
                }
                """;

        doAnswer(returnsFirstArg()).when(saveCategoryUseCase).execute(any());

        // when
        this.graphql.document(query)
                .variable("input", input)
                .execute()
                .path("category.id").entity(String.class).isEqualTo(expectedId)
                .path("category.name").entity(String.class).isEqualTo(expectedName)
                .path("category.description").entity(String.class).isEqualTo(expectedDescription);

        // then
        final var captor = ArgumentCaptor.forClass(Category.class);

        verify(this.saveCategoryUseCase).execute(captor.capture());

        final var actualCategory = captor.getValue();
        assertEquals(expectedId, actualCategory.id());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedActive, actualCategory.active());
        assertEquals(expectedCreatedAt, actualCategory.createdAt());
        assertEquals(expectedUpdatedAt, actualCategory.updatedAt());
        assertEquals(expectedDeletedAt, actualCategory.deletedAt());
    }
}
