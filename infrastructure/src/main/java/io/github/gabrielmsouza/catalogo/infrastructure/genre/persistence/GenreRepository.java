package io.github.gabrielmsouza.catalogo.infrastructure.genre.persistence;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GenreRepository extends ElasticsearchRepository<GenreDocument, String> {
}
