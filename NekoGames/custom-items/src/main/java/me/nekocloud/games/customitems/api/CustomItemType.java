package me.nekocloud.games.customitems.api;

import java.util.Arrays;

public enum CustomItemType {

    THROW_AXE,
    POTION,
    ARROW,
    FEATHER,
    FEATHER_UP;

    public static CustomItemType getByName(String name) {
        name = name.toUpperCase();
        String finalName = name;
        return Arrays.stream(values())
                .filter(type -> type.name().equals(finalName))
                .findFirst()
                .orElse(null);
    }
}
