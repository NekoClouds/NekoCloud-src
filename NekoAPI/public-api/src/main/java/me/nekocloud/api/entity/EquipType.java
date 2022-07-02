package me.nekocloud.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EquipType {
    MAINHAND(0),
    OFFHAND(5),
    FEET(1),
    LEGS(2),
    CHEST(3),
    HEAD(4);

    private final int ID;
}
