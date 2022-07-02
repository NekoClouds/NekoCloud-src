package me.nekocloud.anarchy.listener;

import gnu.trove.map.TIntObjectMap;
import lombok.val;
import me.nekocloud.anarchy.Anarchy;
import me.nekocloud.anarchy.AnarchyBoard;
import me.nekocloud.anarchy.stats.StatsPlayer;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.market.Market;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener extends DListener<Anarchy> {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();
    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

    private final Map<EntityType, Integer> moneyData;
    private final TIntObjectMap<Double> multiData; //level

    private final Anarchy anarchy;

    public PlayerListener(Anarchy anarchy) {
        super(anarchy);
        multiData = anarchy.getAnarchyConfig().loadMultiMoney();
        moneyData = anarchy.getAnarchyConfig().loadMoneyData();

        this.anarchy = anarchy;
    }

    private final Map<String, AnarchyBoard> boards = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinPlayer(AsyncGamerJoinEvent e) {
        Player player = e.getGamer().getPlayer();
        boards.put(player.getName(), new AnarchyBoard(player));
    }

    @EventHandler
    public void onQuitPlayer(AsyncGamerQuitEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null)
            return;

        AnarchyBoard anarchyBoard = boards.remove(player.getName());
        if (anarchyBoard == null)
            return;

        anarchyBoard.remove();
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e) {
        e.blockList().removeIf(block -> block != null && (
                block.getType() == Material.DIAMOND_BLOCK
                || block.getType() == Material.GOLD_BLOCK
                || block.getType() == Material.EMERALD_ORE));
    }


    @EventHandler
    public void onExplodeDrop(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.DROPPED_ITEM) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode2(EntityExplodeEvent e) {
        for (Block block : new ArrayList<>(e.blockList())) {
            if (block == null)
                continue;
            if (block.getType() == Material.DIAMOND_BLOCK
                    || block.getType() == Material.GOLD_BLOCK
                    || block.getType() == Material.EMERALD_ORE)
                e.blockList().remove(block);
        }
    }

    @EventHandler
    public void onKillPlayer(PlayerDeathEvent e) {
        Player death = e.getEntity();
        String deathName = death.getName();

        AnarchyBoard board = boards.get(deathName);
        if (board == null)
            return;
        StatsPlayer statsPlayer = board.getStatsPlayer();
        statsPlayer.addDeath();

        Player killer = death.getKiller();
        if (killer == null)
            return;

        String killerName = killer.getName();

        board = boards.get(killerName);
        if (board == null)
            return;
        board.getStatsPlayer().addKill();
    }

    @EventHandler
    public void onKillMob(EntityDeathEvent e) {
        EntityType entityType = e.getEntityType();
        LivingEntity livingEntity = e.getEntity();
        Player killer = livingEntity.getKiller();
        if (killer == null)
            return;

        if (livingEntity instanceof Animals && !((Animals) livingEntity).isAdult())
            return;

        Integer coast = moneyData.get(entityType);
        if (coast == null)
            return;

        BukkitGamer gamer = gamerManager.getGamer(killer);
        if (gamer == null)
            return;

        MarketPlayer marketPlayer = marketPlayerManager.getMarketPlayer(killer);
        if (marketPlayer == null)
            return;

        Language lang = gamer.getLanguage();

        double money = Market.round(coast * getMulti(gamer.getGroup().getLevel()));
        gamer.sendActionBar(lang.getMessage( "ANARCHY_KILL_MOB", String.valueOf(money),
                CommonWords.COINS_1.convert((int) money, lang)));
        marketPlayer.changeMoney(money);
    }

    private double getMulti(int level) {
        while (level != 0) {
            Double multi = multiData.get(level);
            if (multi == null) {
                level--;
                continue;
            }

            return multi;
        }
        return 1;
    }
}
