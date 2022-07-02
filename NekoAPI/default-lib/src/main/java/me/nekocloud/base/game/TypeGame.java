package me.nekocloud.base.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeGame {
    SOLO(0),
    DOUBLES(1),
    TEAM(2),
    ;

    private final int id;
}
