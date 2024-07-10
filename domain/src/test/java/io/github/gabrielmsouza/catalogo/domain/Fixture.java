package io.github.gabrielmsouza.catalogo.domain;


import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import net.datafaker.Faker;

import java.util.UUID;

public final class Fixture {
    private static final Faker FAKER = new Faker();

    private Fixture() {}

    public static String name() {
        return FAKER.name().fullName();
    }

    public static String title() {
        return FAKER.options().option("Title 1", "Title 2", "Title 3");
    }

    public static int year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static double duration() {
        return FAKER.options().option(120.0, 15.5, 35.5, 10.0, 2.0);
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static String checksum() {
        return "03fe62de";
    }

    public static final class Categories {
        public static Category aulas() {
            return Category.with(
                    IDUtils.uuid(),
                    "Aulas",
                    "Conteudo gravado",
                    true,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    null
            );
        }

        public static Category talks() {
            return Category.with(
                    IDUtils.uuid(),
                    "Talks",
                    "Conteudo ao vivo",
                    false,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    InstantUtils.now()
            );
        }

        public static Category lives() {
            return Category.with(
                    IDUtils.uuid(),
                    "Lives",
                    "Conteudo ao vivo",
                    true,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    null
            );
        }
    }
}
