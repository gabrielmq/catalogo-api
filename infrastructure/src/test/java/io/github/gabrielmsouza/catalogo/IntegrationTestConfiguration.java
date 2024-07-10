package io.github.gabrielmsouza.catalogo;

import io.github.gabrielmsouza.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class IntegrationTestConfiguration {
    @Bean
    public CategoryRepository categoryRepository() {
        return mock(CategoryRepository.class);
    }
}
