package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.persistence.GenreDocument;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

@Component
@Profile("!dev")
public class GenreElasticsearchGateway implements GenreGateway {
    private static final String NAME_PROP = "name";
    private static final String KEYWORD = ".keyword";
    private static final String CATEGORIES_PROP = "categories";

    private final GenreRepository repository;
    private final SearchOperations operations;

    public GenreElasticsearchGateway(
            final GenreRepository repository,
            final SearchOperations operations
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.operations = Objects.requireNonNull(operations);
    }

    @Override
    public Genre save(final Genre aGenre) {
        this.repository.save(GenreDocument.from(aGenre));
        return aGenre;
    }

    @Override
    public void deleteById(final String anId) {
        this.repository.deleteById(anId);
    }

    @Override
    public Optional<Genre> findById(final String anId) {
        return this.repository.findById(anId).map(GenreDocument::toGenre);
    }

    @Override
    public Pagination<Genre> findAll(final GenreSearchQuery aQuery) {
        final var terms = aQuery.terms();
        final var currentPage = aQuery.page();
        final var itemsPerPage = aQuery.perPage();

        final var sort = Sort.by(Direction.fromString(aQuery.direction()), buildSort(aQuery.sort()));
        final var page = PageRequest.of(currentPage, itemsPerPage, sort);

        final Query query = StringUtils.isEmpty(terms) && CollectionUtils.isEmpty(aQuery.categories())
                ? Query.findAll().setPageable(page)
                : new CriteriaQuery(createCriteria(aQuery), page);

        final var res = this.operations.search(query, GenreDocument.class);
        final var total = res.getTotalHits();

        final var genres = res.stream()
                .map(SearchHit::getContent)
                .map(GenreDocument::toGenre)
                .toList();

        return new Pagination<>(currentPage, itemsPerPage, total, genres);
    }

    @Override
    public List<Genre> findAllById(final Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return StreamSupport.stream(this.repository.findAllById(ids).spliterator(), false)
                .map(GenreDocument::toGenre)
                .toList();
    }

    private static Criteria createCriteria(final GenreSearchQuery aQuery) {
        Criteria criteria = null;
        if (StringUtils.isNotEmpty(aQuery.terms())) {
            criteria = Criteria.where(NAME_PROP).contains(aQuery.terms());
        }
        if (!CollectionUtils.isEmpty(aQuery.categories())) {
            final var categoriesWhere = Criteria.where(CATEGORIES_PROP).in(aQuery.categories());
            criteria = criteria != null ? criteria.and(categoriesWhere) : categoriesWhere;
        }
        return criteria;
    }

    private String buildSort(final String sort) {
        return NAME_PROP.equals(sort) ? sort.concat(KEYWORD) : sort;
    }
}
