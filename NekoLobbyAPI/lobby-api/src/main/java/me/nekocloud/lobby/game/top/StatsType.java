package me.nekocloud.lobby.game.top;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatsType {

    ALL(0),
    MONTHLY(1);

    private final int value;
}
