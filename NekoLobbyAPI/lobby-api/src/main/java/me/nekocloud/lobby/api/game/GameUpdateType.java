package me.nekocloud.lobby.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
@Getter
public enum GameUpdateType {
    DEFAULT(null, ChatColor.AQUA),
    UPDATE("GAMEMENU_ITEM_UPDATE_NAME", ChatColor.DARK_AQUA),
    NEW("GAMEMENU_ITEM_NEW_NAME", ChatColor.LIGHT_PURPLE),
    WIPE("GAMEMENU_ITEM_WIPE", ChatColor.AQUA);

    private final String key;
    private final ChatColor chatColor;
}
