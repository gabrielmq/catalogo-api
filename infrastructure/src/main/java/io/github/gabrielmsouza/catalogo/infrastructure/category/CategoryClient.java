package io.github.gabrielmsouza.catalogo.infrastructure.category;

import io.github.gabrielmsouza.catalogo.domain.category.Category;

import java.util.Optional;

public interface CategoryClient {
    Optional<Category> categoryOfId(String anId);
}
