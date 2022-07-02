package pw.novit.nekocloud.bungee.api.bossbar.impl;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BossBarAPIListener implements Listener {

    private final BossBarAPIImpl bossBarAPIImpl;

    @EventHandler(priority=-64)
    public void onDisconnect(@NotNull PlayerDisconnectEvent e) {
        bossBarAPIImpl.getCachedBossBars().remove(e.getPlayer());
    }

}

