package me.nekocloud.games.customitems.api.arrow;

import java.util.Arrays;

public enum CustomArrowEffect {

    HOMING;

    public static CustomArrowEffect getByName(String name) {
        name = name.toUpperCase();
        String finalName = name;
        return Arrays.stream(values())
                .filter(effect -> effect.name().equals(finalName))
                .findFirst()
                .orElse(null);
    }
}
