package me.nekocloud.lobby.cosmetics.api.manager;

import me.nekocloud.lobby.cosmetics.api.player.CosmeticPlayer;
import org.bukkit.entity.Player;

public interface CosmeticManager {

    CosmeticPlayer getCosmeticPlayer(Player player);

    void loadPlayer(Player player);

    void unloadPlayer(Player player);
}

