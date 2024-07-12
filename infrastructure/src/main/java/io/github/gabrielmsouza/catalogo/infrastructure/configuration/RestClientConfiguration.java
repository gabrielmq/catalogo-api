package io.github.gabrielmsouza.catalogo.infrastructure.configuration;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.annontations.Categories;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.annontations.Keycloak;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.properties.RestClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfiguration {
    @Bean
    @Categories
    @ConfigurationProperties("rest-client.categories")
    RestClientProperties categoryRestClientProperties() {
        return new RestClientProperties();
    }

    @Bean
    @Keycloak
    @ConfigurationProperties("rest-client.keycloak")
    RestClientProperties keycloakRestClientProperties() {
        return new RestClientProperties();
    }

    @Bean
    @Categories
    RestClient categoryHttpClient(@Categories final RestClientProperties properties) {
        return restClient(properties);
    }

    @Bean
    @Keycloak
    RestClient keycloakHttpClient(@Keycloak final RestClientProperties properties) {
        return restClient(properties);
    }

    private RestClient restClient(final RestClientProperties properties) {
        final var factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(properties.readTimeout());
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();
    }
}
