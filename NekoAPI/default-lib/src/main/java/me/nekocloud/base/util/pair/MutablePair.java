package me.nekocloud.base.util.pair;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface MutablePair<L, R> extends Pair<L, R> {

    @NotNull
    static <L, R> MutablePair<L, R> of(L left, R right) {
        return Pairs.mutableOf(left, right);
    }

    @Contract("_ -> new")
    @NotNull
    static <L, R> MutablePair<L, R> of(Map.@NotNull Entry<L, R> entry) {
        return Pairs.mutableOf(entry.getKey(), entry.getValue());
    }

    void setLeft(L var1);

    void setRight(R var1);

    @Override
    MutablePair<L, R> copy();
}

