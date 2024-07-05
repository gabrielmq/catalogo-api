package io.github.gabrielmsouza.catalogo;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.ObjectMapperConfiguration;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@Tag("integrationTest")
@ActiveProfiles("test-integration")
@JsonTest(includeFilters = {
    @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = ObjectMapperConfiguration.class)
})
public @interface JacksonTest {
}
