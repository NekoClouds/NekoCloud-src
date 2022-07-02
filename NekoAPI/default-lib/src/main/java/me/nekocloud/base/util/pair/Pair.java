package me.nekocloud.base.util.pair;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Pair<L, R> {
    @NotNull
    static <L, R> Pair<L, R> of(L left, R right) {
        return Pairs.immutableOf(left, right);
    }

    @Contract("_ -> new")
    @NotNull
    static <L, R> Pair<L, R> of(Map.@NotNull Entry<L, R> entry) {
        return Pairs.immutableOf(entry.getKey(), entry.getValue());
    }

    L getLeft();

    R getRight();

    Pair<L, R> copy();
}

