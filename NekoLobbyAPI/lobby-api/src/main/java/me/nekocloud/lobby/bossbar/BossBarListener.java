package me.nekocloud.lobby.bossbar;

import lombok.val;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;

public class BossBarListener extends DListener<Lobby> {

    private final BossBarLobby barLobby;

    public BossBarListener(Lobby lobby) {
        super(lobby);

        barLobby = new BossBarLobby(lobby);
    }

    @EventHandler
    public void onJoin(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();

        val bossBar = barLobby.get(gamer.getLanguage());
        if (bossBar != null) {
            bossBar.addPlayer(gamer.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(AsyncGamerQuitEvent e) {
        val gamer = e.getGamer();

        val bossBar = barLobby.get(gamer.getLanguage());
        if (bossBar != null) {
            bossBar.removePlayer(gamer.getPlayer());
        }
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        val player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        val bossBar = barLobby.get(e.getOldLanguage());
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }

        BukkitUtil.runTaskLaterAsync(5L, () -> {
            BossBar bar = barLobby.get(e.getLanguage());
            if (bar != null) {
                bar.addPlayer(player);
            }
        });
    }
}


