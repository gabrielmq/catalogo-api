package io.github.gabrielmsouza.catalogo.infrastructure.configuration.usecases;

import io.github.gabrielmsouza.catalogo.application.category.delete.DeleteCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.get.GetAllCategoriesByIdUseCase;
import io.github.gabrielmsouza.catalogo.application.category.list.ListCategoryUseCase;
import io.github.gabrielmsouza.catalogo.application.category.save.SaveCategoryUseCase;
import io.github.gabrielmsouza.catalogo.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
public class CategoryUseCaseConfiguration {
    private final CategoryGateway gateway;

    public CategoryUseCaseConfiguration(final CategoryGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Bean
    SaveCategoryUseCase saveCategoryUseCase() {
        return new SaveCategoryUseCase(gateway);
    }

    @Bean
    DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DeleteCategoryUseCase(gateway);
    }

    @Bean
    ListCategoryUseCase listCategoryUseCase() {
        return new ListCategoryUseCase(gateway);
    }

    @Bean
    GetAllCategoriesByIdUseCase getAllCategoriesByIdUseCase() {
        return new GetAllCategoriesByIdUseCase(gateway);
    }
}
