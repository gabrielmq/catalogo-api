package io.github.gabrielmsouza.catalogo.infrastructure.configuration;

import io.github.gabrielmsouza.catalogo.infrastructure.authentication.RefreshClientCredentials;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ComponentScan("io.github.gabrielmsouza.catalogo")
public class WebServerConfiguration {
    @Bean
    @Profile("!test-integration & !test-e2e")
    ApplicationListener<ContextRefreshedEvent> refreshClientCredentials(final RefreshClientCredentials clientCredentials) {
        return event -> clientCredentials.refresh();
    }
}
