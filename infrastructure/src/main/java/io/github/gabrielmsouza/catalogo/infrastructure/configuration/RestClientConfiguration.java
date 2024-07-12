package io.github.gabrielmsouza.catalogo.infrastructure.configuration;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.properties.RestClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfiguration {
    @Bean
    @ConfigurationProperties("rest-client.categories")
    RestClientProperties categoryRestClientProperties() {
        return new RestClientProperties();
    }

    @Bean
    RestClient categoryHttpClient(final RestClientProperties properties) {
        final var factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(properties.readTimeout());
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }
}
