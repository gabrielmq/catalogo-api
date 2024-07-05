package io.github.gabrielmsouza.catalogo;

import io.github.gabrielmsouza.catalogo.infrastructure.configuration.ObjectMapperConfiguration;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@WebMvcTest
@Tag("integrationTest")
@ActiveProfiles("test-integration")
@Target(TYPE)
@Retention(RUNTIME)
@Import(ObjectMapperConfiguration.class)
public @interface ControllerTest {
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
}
