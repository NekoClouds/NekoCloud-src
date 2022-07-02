package me.nekocloud.lobby.cosmetics.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.lobby.cosmetics.api.manager.CosmeticManager;
import me.nekocloud.lobby.cosmetics.api.player.CosmeticPlayer;
import me.nekocloud.lobby.cosmetics.sql.CosmeticLoader;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CraftCosmeticManager implements CosmeticManager {

    GamerManager gamerManager = NekoCloud.getGamerManager();
    Map<String, CosmeticPlayer> players = new ConcurrentHashMap<>();

    @Override
    public CosmeticPlayer getCosmeticPlayer(Player player) {
        return players.get(player.getName());
    }

    @Override
    public void loadPlayer(Player player) {
        val gamer = gamerManager.getGamer(player.getName());
        if (gamer == null)
            return;

        players.put(player.getName(), CosmeticLoader.getCosmeticPlayer(player, gamer.getPlayerID()));
    }

    @Override
    public void unloadPlayer(Player player) {
        players.remove(player.getName());
    }
}

