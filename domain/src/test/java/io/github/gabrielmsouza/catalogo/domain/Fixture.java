package io.github.gabrielmsouza.catalogo.domain;


import io.github.gabrielmsouza.catalogo.domain.castmember.CastMember;
import io.github.gabrielmsouza.catalogo.domain.castmember.CastMemberType;
import io.github.gabrielmsouza.catalogo.domain.category.Category;
import io.github.gabrielmsouza.catalogo.domain.genre.Genre;
import io.github.gabrielmsouza.catalogo.domain.utils.IDUtils;
import io.github.gabrielmsouza.catalogo.domain.utils.InstantUtils;
import io.github.gabrielmsouza.catalogo.domain.video.Rating;
import io.github.gabrielmsouza.catalogo.domain.video.Video;
import net.datafaker.Faker;

import java.util.HashSet;
import java.util.Set;
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

    public static final class CastMembers {

        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }

        public static CastMember actor() {
            return CastMember.with(UUID.randomUUID().toString(), Fixture.name(), CastMemberType.ACTOR, InstantUtils.now(),  InstantUtils.now());
        }

        public static CastMember actor(final String name) {
            return CastMember.with(UUID.randomUUID().toString(), name, CastMemberType.ACTOR, InstantUtils.now(),  InstantUtils.now());
        }

        public static CastMember unknown() {
            return CastMember.with(UUID.randomUUID().toString(), Fixture.name(), CastMemberType.UNKNOWN,  InstantUtils.now(),  InstantUtils.now());
        }

        public static CastMember unknown(final String name) {
            return CastMember.with(UUID.randomUUID().toString(), name, CastMemberType.UNKNOWN,  InstantUtils.now(),  InstantUtils.now());
        }

        public static CastMember director() {
            return CastMember.with(UUID.randomUUID().toString(), Fixture.name(), CastMemberType.DIRECTOR,  InstantUtils.now(),  InstantUtils.now());
        }

        public static CastMember director(final String name) {
            return CastMember.with(UUID.randomUUID().toString(), name, CastMemberType.DIRECTOR,  InstantUtils.now(),  InstantUtils.now());
        }
    }

    public static final class Genres {

        public static Genre tech() {
            return Genre.with(IDUtils.uuid(), "Technology", true, Set.of("c456"), InstantUtils.now(), InstantUtils.now(), null);
        }

        public static Genre business() {
            return Genre.with(IDUtils.uuid(), "Business", false, new HashSet<>(), InstantUtils.now(), InstantUtils.now(), InstantUtils.now());
        }

        public static Genre marketing() {
            return Genre.with(IDUtils.uuid(), "Marketing", true, Set.of("c123"), InstantUtils.now(), InstantUtils.now(), null);
        }
    }

    public static final class Videos {

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        public static Video systemDesign() {
            return Video.with(
                    IDUtils.uuid(),
                    "System Design no Mercado Livre na prática",
                    "O vídeo mais assistido",
                    2022,
                    Fixture.duration(),
                    Rating.AGE_16.getName(),
                    true,
                    true,
                    InstantUtils.now().toString(),
                    InstantUtils.now().toString(),
                    "http://video",
                    "http://trailer",
                    "http://banner",
                    "http://thumb",
                    "http://thumbhalf",
                    Set.of("aulas"),
                    Set.of("luiz"),
                    Set.of("systemdesign")
            );
        }

        public static Video java21() {
            return Video.with(
                    IDUtils.uuid(),
                    "Java 21",
                    "Java FTW",
                    2023,
                    Fixture.duration(),
                    Rating.AGE_10.getName(),
                    true,
                    true,
                    InstantUtils.now().toString(),
                    InstantUtils.now().toString(),
                    "http://video",
                    "http://trailer",
                    "http://banner",
                    "http://thumb",
                    "http://thumbhalf",
                    Set.of("lives"),
                    Set.of("gabriel"),
                    Set.of("java")
            );
        }

        public static Video golang() {
            return Video.with(
                    IDUtils.uuid(),
                    "Golang 1.22",
                    "Um vídeo da linguagem go",
                    2024,
                    Fixture.duration(),
                    Rating.L.getName(),
                    true,
                    true,
                    InstantUtils.now().toString(),
                    InstantUtils.now().toString(),
                    "http://video",
                    "http://trailer",
                    "http://banner",
                    "http://thumb",
                    "http://thumbhalf",
                    Set.of("meeting"),
                    Set.of("wesley"),
                    Set.of("golang")
            );
        }
    }
}
