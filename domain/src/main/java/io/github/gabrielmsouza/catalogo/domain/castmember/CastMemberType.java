package io.github.gabrielmsouza.catalogo.domain.castmember;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CastMemberType {
    ACTOR, DIRECTOR, UNKNOWN;

    private static final Map<String, CastMemberType> CAST_MEMBER_TYPES =
            Stream.of(values()).collect(Collectors.toMap(CastMemberType::name, Function.identity()));

    public static CastMemberType of(final String aType) {
        return CAST_MEMBER_TYPES.getOrDefault(aType, UNKNOWN);
    }
}
