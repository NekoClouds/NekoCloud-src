package me.nekocloud.lobby.profile;

import lombok.val;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.config.SettingConfig;
import me.nekocloud.lobby.utils.LevelUtils;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashSet;

public class PlayerListener extends DListener<Lobby> {

    private final SettingConfig settingConfig;

    public PlayerListener(Lobby lobby, SettingConfig settingConfig) {
        super(lobby);
        this.settingConfig = settingConfig;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoinAsync(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();

        val lang = gamer.getLanguage();
        LevelUtils.setExpData(gamer);
        gamer.sendTitle(lang.getMessage("TITLE_JOIN_LOBBY"), lang.getMessage("SUBTITLE_JOIN_LOBBY"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        val player = e.getPlayer();

        BukkitUtil.runTaskLaterAsync(10L, () -> player.setCompassTarget(settingConfig.getSpawn()));

        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (!gamer.getSetting(SettingsType.CHAT)) {
            gamer.sendMessagesLocale("CHAT_LOBBY_OFF");
        }

        if (gamer.isAkio() && gamer.getSetting(SettingsType.FLY)) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if (gamer.getSetting(SettingsType.BOARD)) {
            val boardLobby = LobbyAPI.getBoardLobby();
            if (boardLobby == null) {
                return;
            }

            boardLobby.showBoard(gamer, gamer.getLanguage());
        }
    }

    @EventHandler
    public void onSetSpawn(PlayerSpawnLocationEvent e) {
        e.setSpawnLocation(settingConfig.getSpawn());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent e) {
        val player = e.getPlayer();
        if (player.getLocation().getY() <= 0) {
            player.teleport(settingConfig.getSpawn());
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        val player = e.getPlayer();
        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        if (player.getWorld().getName().equals("lobby")) {
            if (gamer.isAkio() && !gamer.getSetting(SettingsType.FLY)) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        } else if (gamer.isAkio() && !gamer.getSetting(SettingsType.FLY)) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        val lang = e.getLanguage();
        val gamer = e.getGamer();

        if (Cooldown.hasOrAddCooldown(gamer, "cooldown", 5)) {
            return;
        }

        gamer.sendMessage(lang.getMessage("LANGUAGE_CHANGE", lang.getName()));
        val boardLobby = LobbyAPI.getBoardLobby();
        if (boardLobby == null || !gamer.getSetting(SettingsType.BOARD)) {
            return;
        }

        boardLobby.showBoard(gamer, lang);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        val player = e.getPlayer();

        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (!gamer.getSetting(SettingsType.CHAT)) {
            gamer.sendMessageLocale("LOBBY_DISABLED_MESSAGE_TO_CHAT");
            e.setCancelled(true);
            return;
        }

        boolean youTube = gamer.getGroup().getLevel() >= Group.YOUTUBE.getLevel();
        for (val target : new HashSet<>(e.getRecipients())) {
            if (target == player) {
                continue;
            }

            val targetGamer = GAMER_MANAGER.getGamer(target);
            if (targetGamer == null || targetGamer.getSetting(SettingsType.CHAT) || youTube) {
                continue;
            }

            e.getRecipients().remove(target);
        }
    }
}
