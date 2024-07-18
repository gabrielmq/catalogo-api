package io.github.gabrielmsouza.catalogo.domain.video;

import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface VideoGateway {
    Video save(Video video);
    void deleteById(String videoId);
    Optional<Video> findById(String videoId);
    Pagination<Video> findAll(VideoSearchQuery aQuery);
}
