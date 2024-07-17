package io.github.gabrielmsouza.catalogo.infrastructure.video;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

public final class VideoQueryBuilder {
    private static final Option NOOP = b -> {};

    private final List<Query> must;

    public VideoQueryBuilder(Option ...opts) {
        this.must = new ArrayList<>();
        for (final Option opt : opts) {
            opt.accept(this);
        }
    }

    public VideoQueryBuilder must(final Query query) {
        this.must.add(query);
        return this;
    }

    public static Option onlyPublished() {
        return b -> b.must(term(t -> t.field("published").value(true)));
    }

    public static Option containingCastMembers(final Set<String> castMembers) {
        return !CollectionUtils.isEmpty(castMembers)
                ? b -> b.must(terms(t -> t.field("cast_members").terms(it -> it.value(fieldValues(castMembers)))))
                : NOOP;
    }

    public static Option containingCategories(final Set<String> categories) {
        return !CollectionUtils.isEmpty(categories)
                ? b -> b.must(terms(t -> t.field("categories").terms(it -> it.value(fieldValues(categories)))))
                : NOOP;
    }

    public static Option containingGenres(final Set<String> genres) {
        return !CollectionUtils.isEmpty(genres)
                ? b -> b.must(terms(t -> t.field("genres").terms(it -> it.value(fieldValues(genres)))))
                : NOOP;
    }

    public static Option launchedAtEquals(final Integer launchedAt) {
        return Objects.nonNull(launchedAt)
                ? b -> b.must(term(t -> t.field("launched_at").value(launchedAt)))
                : NOOP;
    }

    public static Option ratingEquals(final String rating) {
        return Objects.nonNull(rating) && !rating.isBlank()
                ? b -> b.must(term(t -> t.field("rating").value(rating)))
                : NOOP;
    }

    public static Option titleOrDescriptionContaining(final String terms) {
        return Objects.nonNull(terms) && !terms.isBlank()
                ? b -> b.must(queryString(q -> q.fields("title", "description").query("*" + terms + "*")))
                : NOOP;
    }

    public Query build() {
        return QueryBuilders.bool(b -> b.must(must));
    }
    public interface Option extends Consumer<VideoQueryBuilder> {}

    private static List<FieldValue> fieldValues(final Set<String> ids) {
        return ids.stream().map(FieldValue::of).toList();
    }
}
