package io.github.gabrielmsouza.catalogo.infrastructure.video.persistence;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VideoRepository extends ElasticsearchRepository<VideoDocument, String> {
}
