package io.github.gabrielmsouza.catalogo.infrastructure.castmember;

import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberGateway;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberSearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("dev")
public class CastMemberInMemoryGateway implements CastMemberGateway {
    private final Map<String, CastMember> db;

    public CastMemberInMemoryGateway() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public CastMember save(final CastMember aCastMember) {
        this.db.put(aCastMember.id(), aCastMember);
        return aCastMember;
    }

    @Override
    public void deleteById(final String anId) {
        this.db.remove(anId);
    }

    @Override
    public Optional<CastMember> findById(final String anId) {
        return Optional.ofNullable(this.db.get(anId));
    }

    @Override
    public Pagination<CastMember> findAll(final CastMemberSearchQuery aQuery) {
        return new Pagination<>(
                aQuery.page(),
                aQuery.perPage(),
                this.db.values().size(),
                this.db.values().stream().toList()
        );
    }

    @Override
    public List<CastMember> findAllById(Set<String> genreId) {
        if (genreId == null || genreId.isEmpty()) {
            return List.of();
        }
        return genreId.stream().map(this.db::get).toList();
    }
}
