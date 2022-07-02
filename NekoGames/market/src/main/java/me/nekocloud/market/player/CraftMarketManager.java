package me.nekocloud.market.player;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;
import me.nekocloud.market.utils.PlayerLoader;
import org.bukkit.entity.Player;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftMarketManager implements MarketPlayerManager {

    private final Map<Integer, MarketPlayer> marketPlayers = new ConcurrentHashMap<>();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    @Override
    public MarketPlayer getMarketPlayer(int playerID) {
        return marketPlayers.get(playerID);
    }

    @Override
    public MarketPlayer getMarketPlayer(String name) {
        BukkitGamer gamer = gamerManager.getGamer(name);
        if (gamer == null)
            return null;

        return getMarketPlayer(gamer.getPlayerID());
    }

    @Override
    public MarketPlayer getMarketPlayer(Player player) {
        return getMarketPlayer(player.getName());
    }

    @Override
    public MarketPlayer getOrCreate(String name) {
        MarketPlayer marketPlayer = getMarketPlayer(name);
        if (marketPlayer == null)
            return PlayerLoader.get(name);

        return marketPlayer;
    }

    @Override
    public MarketPlayer getOrCreate(@NotNull IBaseGamer gamer) {
        return getOrCreate(gamer.getName());
    }

    @Override
    public void addMarketPlayer(MarketPlayer marketPlayer) {
        String name = marketPlayer.getName();
        BukkitGamer gamer = gamerManager.getGamer(name);
        if (gamer == null)
            return;

        int playerID = gamer.getPlayerID();
        if (marketPlayers.containsKey(playerID))
            return;

        marketPlayers.put(playerID, marketPlayer);
    }

    @Override
    public void removeMarketPlayer(MarketPlayer marketPlayer) {
        String name = marketPlayer.getName();
        removeMarketPlayer(name);
    }

    @Override
    public void removeMarketPlayer(String name) {
        BukkitGamer gamer = gamerManager.getGamer(name);
        if (gamer == null)
            return;

        marketPlayers.remove(gamer.getPlayerID());
    }

    @Override
    public Map<Integer, MarketPlayer> getMarketPlayers() {
        return marketPlayers;
    }

    @Override
    public boolean contains(String name) {
        BukkitGamer gamer = gamerManager.getGamer(name);
        if (gamer == null)
            return false;

        return marketPlayers.containsKey(gamer.getPlayerID());
    }
}
