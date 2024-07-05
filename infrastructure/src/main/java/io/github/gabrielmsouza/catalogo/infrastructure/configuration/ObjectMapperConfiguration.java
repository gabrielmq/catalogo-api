package io.github.gabrielmsouza.catalogo.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielmsouza.catalogo.infrastructure.configuration.json.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return Json.mapper();
    }
}
