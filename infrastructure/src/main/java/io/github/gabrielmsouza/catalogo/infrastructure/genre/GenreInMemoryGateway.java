package io.github.gabrielmsouza.catalogo.infrastructure.genre;

import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreGateway;
import io.github.gabrielmsouza.catalogo.domain.genre.GenreSearchQuery;
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
public class GenreInMemoryGateway implements GenreGateway {
    private final Map<String, Genre> db;

    public GenreInMemoryGateway() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public Genre save(final Genre aGenre) {
        this.db.put(aGenre.id(), aGenre);
        return aGenre;
    }

    @Override
    public void deleteById(final String anId) {
        this.db.remove(anId);
    }

    @Override
    public Optional<Genre> findById(final String anId) {
        return Optional.ofNullable(this.db.get(anId));
    }

    @Override
    public Pagination<Genre> findAll(final GenreSearchQuery aQuery) {
        return new Pagination<>(
                aQuery.page(),
                aQuery.perPage(),
                this.db.values().size(),
                this.db.values().stream().toList()
        );
    }

    @Override
    public List<Genre> findAllById(Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().map(this.db::get).toList();
    }
}
