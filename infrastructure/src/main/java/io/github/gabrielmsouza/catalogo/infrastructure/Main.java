package io.github.gabrielmsouza.catalogo.infrastructure;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.WebServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.core.env.AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty(DEFAULT_PROFILES_PROPERTY_NAME, "dev");
        SpringApplication.run(WebServerConfiguration.class, args);
    }
}