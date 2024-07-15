package io.github.gabrielmsouza.catalogo.infrastructure.castmember;

import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.persistence.CastMemberDocument;
import io.github.gabrielmsouza.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.elasticsearch.core.query.Criteria.where;

@Component
public class CastMemberElasticsearchGateway implements CastMemberGateway {
    private static final String NAME_PROP = "name";
    private static final String KEYWORD = ".keyword";

    private final CastMemberRepository repository;
    private final SearchOperations searchOperations;

    public CastMemberElasticsearchGateway(
            final CastMemberRepository repository,
            final SearchOperations searchOperations
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.searchOperations = Objects.requireNonNull(searchOperations);
    }

    @Override
    public CastMember save(final CastMember aCastMember) {
        this.repository.save(CastMemberDocument.from(aCastMember));
        return aCastMember;
    }

    @Override
    public void deleteById(final String anId) {
        this.repository.deleteById(anId);
    }

    @Override
    public Optional<CastMember> findById(final String anId) {
        return this.repository.findById(anId).map(CastMemberDocument::toCastMember);
    }

    @Override
    public Pagination<CastMember> findAll(final CastMemberSearchQuery aQuery) {
        final var terms = aQuery.terms();
        final var currentPage = aQuery.page();
        final var perPage = aQuery.perPage();

        final var sort = Sort.by(Sort.Direction.fromString(aQuery.direction()), buildSort(aQuery.sort()));
        final var page = PageRequest.of(currentPage, perPage, sort);

        final var query = StringUtils.isNotEmpty(terms)
                ? new CriteriaQuery(where("name").contains(terms), page)
                : Query.findAll().setPageable(page);

        final var res = this.searchOperations.search(query, CastMemberDocument.class);
        final var total = res.getTotalHits();
        final var members = res.stream()
                .map(SearchHit::getContent)
                .map(CastMemberDocument::toCastMember)
                .toList();

        return new Pagination<>(currentPage, perPage, total, members);
    }

    private String buildSort(final String sort) {
        return NAME_PROP.equals(sort) ? sort.concat(KEYWORD) : sort;
    }
}
