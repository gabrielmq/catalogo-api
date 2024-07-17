package io.github.gabrielmsouza.catalogo.domain.category;

import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryGateway {
    Category save(Category aCategory);
    void deleteById(String anId);
    Optional<Category> findById(String anId);
    Pagination<Category> findAll(CategorySearchQuery aQuery);
    List<Category> findAllById(Set<String> ids);
}
