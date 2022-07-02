package me.nekocloud.base.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.text.format.TextColor;

@AllArgsConstructor
@Getter
public enum GameUpdateType {

    DEFAULT(null, TextColor.AQUA),
    UPDATE("GAMEMENU_ITEM_UPDATE_NAME", TextColor.YELLOW),
    NEW("GAMEMENU_ITEM_NEW_NAME", TextColor.RED),
    WIPE("GAMEMENU_ITEM_WIPE", TextColor.YELLOW),
    SOON("GAMEMENU_ITEM_SOON", TextColor.RED);

    private final String key;
    private final TextColor color;
}

