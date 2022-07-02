package me.nekocloud.base.util;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Pair<A, B> {

    private A first;
    private B second;

    public <C> Pair<C, B> mapFirst(@NotNull Function<A, C> mapFirst) {
        return new Pair<C, B>(mapFirst.apply(this.first), this.second);
    }

    public <C, D> Pair<C, D> map(@NotNull Function<A, C> mapFirst, @NotNull Function<B, D> mapSecond) {
        return new Pair<C, D>(mapFirst.apply(this.first), mapSecond.apply(this.second));
    }


}
