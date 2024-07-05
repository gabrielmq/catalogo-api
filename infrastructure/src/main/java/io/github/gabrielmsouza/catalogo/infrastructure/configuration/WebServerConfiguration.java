package io.github.gabrielmsouza.catalogo.infrastructure.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan("io.github.gabrielmsouza.catalogo")
public class WebServerConfiguration {
}
