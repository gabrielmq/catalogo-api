package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import io.github.gabrielmsouza.catalogo.domain.category.CategorySearchQuery;
import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("dev")
public class CategoryInMemoryGateway implements CategoryGateway {
    private final Map<String, Category> db;

    public CategoryInMemoryGateway() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public Category save(final Category aCategory) {
        this.db.put(aCategory.id(), aCategory);
        return aCategory;
    }

    @Override
    public void deleteById(final String anId) {
        this.db.remove(anId);
    }

    @Override
    public Optional<Category> findById(final String anId) {
        return Optional.ofNullable(this.db.get(anId));
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
        return new Pagination<>(
                aQuery.page(),
                aQuery.perPage(),
                this.db.values().size(),
                this.db.values().stream().toList()
        );
    }
}
