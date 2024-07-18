package io.github.gabrielmsouza.catalogo.infrastructure.video;

import io.github.gabrielmsouza.catalogo.infrastructure.video.models.VideoDTO;

import java.util.Optional;

public interface VideoClient {
    Optional<VideoDTO> videoOfId(String videoId);
}
