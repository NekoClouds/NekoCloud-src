package me.nekocloud.core.resumer;

import lombok.val;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerJoinEvent;
import me.nekocloud.core.api.event.player.PlayerQuitEvent;
import me.nekocloud.core.api.event.player.PlayerSwitchServerEvent;

public class ResumerListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        val player = e.getCorePlayer();
    }

    @EventHandler
    public void onPlayerRedirect(PlayerSwitchServerEvent e) {
        val player = e.getCorePlayer();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        val player = e.getCorePlayer();
    }
}
