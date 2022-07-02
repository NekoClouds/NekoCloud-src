package me.nekocloud.myserver.listener;

import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.common.events.player.PlayerLeaveEvent;
import me.nekocloud.core.common.events.player.PlayerServerRedirectEvent;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.connection.server.impl.BukkitServer;
import me.nekocloud.myserver.type.MyServerManager;

public class MyServerListener implements EventListener {

    @EventHandler
    public void onPlayerRedirect(PlayerServerRedirectEvent event) {
        CorePlayer player = event.getCorePlayer();
        BukkitServer serverFrom = event.getServerFrom();

        checkServer(player, serverFrom);
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        CorePlayer player = event.getCorePlayer();
        BukkitServer serverFrom = player.getBukkitServer();

        checkServer(player, serverFrom);
    }

    private void checkServer(CorePlayer player, BukkitServer bukkitServer) {
        if (bukkitServer == null) {
            return;
        }

        if (MyServerManager.INSTANCE.isLeader(bukkitServer.getName(), player)) {
            MyServerManager.INSTANCE.getPlayerServer(player).shutdown();
        }

        else if (MyServerManager.INSTANCE.isModer(bukkitServer.getName(), player)) {
            MyServerManager.INSTANCE.getPlayerServer(player).removeModer(player);
        }
    }
}
