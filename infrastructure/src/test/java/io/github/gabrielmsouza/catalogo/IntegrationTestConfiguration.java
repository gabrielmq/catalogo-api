package io.github.gabrielmsouza.catalogo;

import io.github.gabrielmsouza.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryRepository;
import io.github.gabrielmsouza.catalogo.infrastructure.genre.persistence.GenreRepository;
import io.github.gabrielmsouza.catalogo.infrastructure.video.persistence.VideoRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class IntegrationTestConfiguration {
    @Bean
    CategoryRepository categoryRepository() {
        return mock(CategoryRepository.class);
    }

    @Bean
    CastMemberRepository castMemberRepository() {
        return mock(CastMemberRepository.class);
    }

    @Bean
    GenreRepository genreRepository() {
        return mock(GenreRepository.class);
    }

    @Bean
    VideoRepository videoRepository() {
        return mock(VideoRepository.class);
    }
}
