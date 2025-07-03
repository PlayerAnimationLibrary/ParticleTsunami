package org.mesdag.particlestorm;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ParticleStorm {
    public static final Codec<List<String>> STRING_LIST_CODEC = Codec.either(Codec.STRING, Codec.list(Codec.STRING)).xmap(
            either -> either.map(Collections::singletonList, Function.identity()),
            l -> l.size() == 1 ? Either.left(l.getFirst()) : Either.right(l)
    );
}
