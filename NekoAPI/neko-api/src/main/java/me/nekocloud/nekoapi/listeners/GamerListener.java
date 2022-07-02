package me.nekocloud.nekoapi.listeners;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerLoadSectionEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerPreLoginEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.scoreboard.PlayerTag;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.sections.*;
import me.nekocloud.entity.BukkitGamerImpl;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GamerListener extends DListener<NekoAPI> {

    ScoreBoardAPI scoreBoardAPI = NekoCloud.getScoreBoardAPI();
    Spigot spigot = NekoCloud.getGamerManager().getSpigot();

    Map<String, BukkitGamerImpl> gamers = new ConcurrentHashMap<>();

    ImmutableSet<Class<? extends Section>> loadedSections = ImmutableSet.of(
            MoneySection.class,
            NetworkingSection.class,
            JoinMessageSection.class,
            FriendsSection.class
    );

    public GamerListener(final NekoAPI nekoAPI) {
        super(nekoAPI);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void loadData(final @NotNull AsyncPlayerPreLoginEvent e) {
        val name = e.getName();
        if (Bukkit.getServer().hasWhitelist()) {
            val offlinePlayer = Bukkit.getOfflinePlayer(name);

            if (offlinePlayer.getName().equalsIgnoreCase("_Novit_")) {
                Bukkit.getServer().getWhitelistedPlayers().add(offlinePlayer);
            }

            if (!offlinePlayer.isWhitelisted() && !name.equalsIgnoreCase("_Novit_")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§5На сервере ведутся тех работы");
                return;
            }
        }

        GamerAPI.removeOfflinePlayer(name);
        BukkitGamer gamer = null;
        try {
            gamer = new BukkitGamerImpl(e); //создаем геймера
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (gamer == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cОшибка при загрузке данных");
            return;
        }
        BukkitUtil.callEvent(new AsyncGamerPreLoginEvent(gamer, e));
        //if (SubType.current == SubType.HNS && !gamer.isGold()) {
        //    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cОшибка, вход доступен только §e§lGOLD §cи выше");
        //    GamerAPI.removeGamer(name);
        //}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoadGamer(final @NotNull AsyncGamerPreLoginEvent e) {
        val gamer = (BukkitGamerImpl) e.getGamer();

        spigot.sendMessage("§fДанные игрока §b" + gamer.getName() + "§r загружены за (§b"
                + (System.currentTimeMillis() - gamer.getStart()) + "ms§f)");

        gamers.put(gamer.getName().toLowerCase(), gamer);
    }

    @EventHandler
    public void onLoadSection(final @NotNull AsyncGamerLoadSectionEvent e) {
        e.setSections(loadedSections); //инициализируем дополнительные секции которые должны быть загружены
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGlobalLogin(final @NotNull PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        val player = e.getPlayer();

        val gamer = gamers.remove(player.getName().toLowerCase());
        if (gamer == null) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cОшибка при загрузке данных");
            return;
        }

        if (gamer.isAdmin() && gamer.isDeveloper()) {
            player.setOp(true);
        } else {
            PermissionAttachment attachment = player.addAttachment(javaPlugin);
            attachment.setPermission("bukkit.command.version", false);
            attachment.setPermission("bukkit.command.plugins", false);
            attachment.setPermission("minecraft.command.help", false);
            attachment.setPermission("bukkit.command.help", false);
            attachment.setPermission("minecraft.command.me", false);
            attachment.setPermission("bukkit.command.me", false);
            attachment.setPermission("minecraft.command.tell", false);
            attachment.setPermission("bukkit.command.tell", false);
        }

        gamer.setPlayer(player);
        player.setDisplayName(gamer.getDisplayName());

        GamerAPI.addGamer(gamer); //вкладываем его в мапу бейс апи
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGlobalJoin(final @NotNull PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();

        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            player.kickPlayer("§cОшибка при загрузке данных");
            return;
        }

        BukkitUtil.runTaskAsync(() -> {
            if (!player.isOnline()) {
                return;
            }

            if (!NekoCloud.isGame()) {
                PlayerTag playerTag = scoreBoardAPI.createTag(scoreBoardAPI.getPriorityScoreboardTag(
                        gamer.getGroup()) + player.getName());
                playerTag.addPlayerToTeam(player);
                playerTag.setPrefix(gamer.getPrefix());
                playerTag.disableCollidesForAll();
                scoreBoardAPI.setDefaultTag(player, playerTag);
            }

            BukkitUtil.callEvent(new AsyncGamerJoinEvent(gamer));
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final @NotNull PlayerQuitEvent e) {
        e.setQuitMessage(null);
        val player = e.getPlayer();

        scoreBoardAPI.removeDefaultTag(player);

        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer != null) {
            BukkitUtil.runTaskAsync(() -> BukkitUtil.callEvent(new AsyncGamerQuitEvent(gamer)));
        }

        if (NekoCloud.isGame()) {
            return;
        }

        GAMER_MANAGER.removeGamer(player);
    }
}
