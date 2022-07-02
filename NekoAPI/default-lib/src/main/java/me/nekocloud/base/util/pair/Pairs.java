package me.nekocloud.base.util.pair;

import lombok.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class Pairs {

    @NotNull
    public static <L, R> Pair<L, R> immutableOf(L left, R right) {
        return new PairImpl<>(left, right);
    }

    @NotNull
    public static <L, R> MutablePair<L, R> mutableOf(L left, R right) {
        return new MutablePairImpl<>(left, right);
    }

    @AllArgsConstructor
    @Getter
    private static final class PairImpl<L, R> extends AbstractPair<L, R> {

        private final L left;
        private final R right;

        @Contract(" -> new")
        @Override
        public @NotNull Pair<L, R> copy() {
            return new PairImpl<>(left, right);
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static final class MutablePairImpl<L, R> extends AbstractPair<L, R> implements MutablePair<L, R> {

        private L left;
        private R right;

        @Contract(" -> new")
        @Override
        public @NotNull MutablePair<L, R> copy() {
            return new MutablePairImpl<L, R>(this.left, this.right);
        }

    }

    @EqualsAndHashCode
    @NoArgsConstructor
    @ToString
    private static abstract class AbstractPair<L, R> implements Pair<L, R> { }
}

