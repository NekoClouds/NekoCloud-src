package me.nekocloud.nekoapi.stats.playtime;

import lombok.val;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class PlayTimeListener extends DListener<NekoAPI> {

    private final PlayTimeManager playTimeManager;

    public PlayTimeListener(final @NotNull PlayTimeManager playTimeManager) {
        super(playTimeManager.getNekoAPI());

        this.playTimeManager = playTimeManager;
    }

    @EventHandler
    public void onGamerJoin(final @NotNull AsyncGamerJoinEvent e) {
        val playtime = StatsLoader.getTime(e.getGamer().getPlayerID());
        BukkitUtil.runTaskAsync(() -> {
            playTimeManager.getPlayerOnlineTime().put(e.getGamer().getPlayerID(), playtime);
            playTimeManager.getLastCheckedTime().put(e.getGamer().getPlayerID(), System.currentTimeMillis());
        });
    }

    @EventHandler
    public void onGamerQuit(final @NotNull AsyncGamerQuitEvent e) {
        playTimeManager.forceSave(e.getGamer().getPlayerID());

        playTimeManager.getPlayerOnlineTime().remove(e.getGamer().getPlayerID());
        playTimeManager.getLastCheckedTime().remove(e.getGamer().getPlayerID());
    }
}
