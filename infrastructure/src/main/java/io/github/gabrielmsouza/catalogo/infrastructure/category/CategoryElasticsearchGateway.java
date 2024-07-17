package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryDocument;
import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import static org.springframework.data.elasticsearch.core.query.Criteria.where;

@Component
@Profile("!dev")
public class CategoryElasticsearchGateway implements CategoryGateway {
    private static final String NAME_PROP = "name";
    private static final String KEYWORD = ".keyword";

    private final CategoryRepository repository;
    private final SearchOperations searchOperations;

    public CategoryElasticsearchGateway(
            final CategoryRepository repository,
            final SearchOperations searchOperations
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.searchOperations = Objects.requireNonNull(searchOperations);
    }

    @Override
    public Category save(final Category aCategory) {
        this.repository.save(CategoryDocument.from(aCategory));
        return aCategory;
    }

    @Override
    public void deleteById(final String anId) {
        this.repository.deleteById(anId);
    }

    @Override
    public Optional<Category> findById(final String anId) {
        return this.repository.findById(anId).map(CategoryDocument::toCategory);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
        final var terms = aQuery.terms();
        final var currentPage = aQuery.page();
        final var perPage = aQuery.perPage();

        final var sort = Sort.by(Sort.Direction.fromString(aQuery.direction()), buildSort(aQuery.sort()));
        final var page = PageRequest.of(currentPage, perPage, sort);

        final var query = buildQuery(terms, page);

        final var res = this.searchOperations.search(query, CategoryDocument.class);
        final var total = res.getTotalHits();
        final var categories = res.stream()
                .map(SearchHit::getContent)
                .map(CategoryDocument::toCategory)
                .toList();

        return new Pagination<>(currentPage, perPage, total, categories);
    }

    private Query buildQuery(final String terms, final  PageRequest page) {
        if (StringUtils.isNotEmpty(terms)) {
            final var criteria = where("name").contains(terms)
                    .or(where("description").contains(terms));

            return new CriteriaQuery(criteria, page);
        }
        return Query.findAll().setPageable(page);
    }

    @Override
    public List<Category> findAllById(final Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return StreamSupport.stream(this.repository.findAllById(ids).spliterator(), false)
                .map(CategoryDocument::toCategory)
                .toList();
    }

    private String buildSort(final String sort) {
        return NAME_PROP.equals(sort) ? sort.concat(KEYWORD) : sort;
    }
}
