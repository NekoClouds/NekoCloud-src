package me.nekocloud.market.player;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.market.Market;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;
import me.nekocloud.market.utils.PlayerLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    private final Market market;

    public PlayerListener(Market market) {
        this.market = market;
        Bukkit.getPluginManager().registerEvents(this, market);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();
        if (gamer == null) {
            return;
        }

        val marketPlayer = PlayerLoader.get(gamer.getName());
        if (marketPlayer == null) {
            e.getGamer().getPlayer().kickPlayer("§c[Market] Ошибка при загрузке данных");
            return;
        }
        
        marketPlayerManager.addMarketPlayer(marketPlayer);

        if (marketPlayerManager.contains(gamer.getName()))
            return;

        gamer.getPlayer().kickPlayer("§cПерезайдите, ошибка при загрузке данных");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getName().toLowerCase();
        marketPlayerManager.removeMarketPlayer(name);

        market.getAuctionManager().removePlayerGuis(player);
        market.getAuctionManager().getOwnerGuis().remove(name);
    }
}
