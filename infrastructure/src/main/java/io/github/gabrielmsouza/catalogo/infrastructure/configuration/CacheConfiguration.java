package io.github.gabrielmsouza.catalogo.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.Cache2kBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@EnableCaching
@Configuration(proxyBeanMethods = false)
public class CacheConfiguration {
    @Bean
    @ConditionalOnProperty(name = "cache.type", havingValue = "cache2k")
    Cache2kBuilderCustomizer cache2kBuilderCustomizer(
            @Value("${cache.max-entries}") final int maxEntries,
            @Value("${cache.ttl}") final int ttl
    ) {
        return builder -> builder
                .entryCapacity(maxEntries)
                .expireAfterWrite(Duration.ofSeconds(ttl));
    }
}
