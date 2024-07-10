package io.github.gabrielmsouza.catalogo.infrastructure;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.WebServerConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Inherited
@Tag("integrationTest")
@ActiveProfiles("test-integration")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = WebServerConfiguration.class)
public @interface IntegrationTest {
}
