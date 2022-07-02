package me.nekocloud.core.webmodule.domianstats;

import lombok.val;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerJoinEvent;

public class DomianListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        val player = e.getCorePlayer();
    }

}
