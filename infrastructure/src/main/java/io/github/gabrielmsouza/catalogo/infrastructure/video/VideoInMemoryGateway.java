package io.github.gabrielmsouza.catalogo.infrastructure.video;

import io.github.gabrielmsouza.catalogo.domain.pagination.Pagination;
import io.github.gabrielmsouza.catalogo.domain.video.Video;
import io.github.gabrielmsouza.catalogo.domain.video.VideoGateway;
import io.github.gabrielmsouza.catalogo.domain.video.VideoSearchQuery;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("dev")
public class VideoInMemoryGateway implements VideoGateway {
    private final Map<String, Video> db;

    public VideoInMemoryGateway() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public Video save(final Video video) {
        this.db.put(video.id(), video);
        return video;
    }

    @Override
    public void deleteById(final String videoId) {
        this.db.remove(videoId);
    }

    @Override
    public Optional<Video> findById(final String videoId) {
        return Optional.ofNullable(this.db.get(videoId));
    }

    @Override
    public Pagination<Video> findAll(VideoSearchQuery aQuery) {
        return new Pagination<>(
                aQuery.page(),
                aQuery.perPage(),
                this.db.size(),
                this.db.values().stream().toList()
        );
    }
}
