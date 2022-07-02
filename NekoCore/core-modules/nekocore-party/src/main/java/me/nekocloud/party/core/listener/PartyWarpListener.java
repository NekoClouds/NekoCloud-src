package me.nekocloud.party.core.listener;

import lombok.val;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerRedirectEvent;
import me.nekocloud.party.core.type.PartyManager;

public final class PartyWarpListener
        implements EventListener {

    @EventHandler
    public void onPlayerRedirect(PlayerRedirectEvent event) {
        val player = event.getCorePlayer();
        val party = PartyManager.INSTANCE.getParty(player);

        val bukkitServer = event.getTo();

//        if (canWarp(bukkitServer) && party != null && party.isLeader(player)) {
//            party.warp(bukkitServer);
//        }
    }

//    private boolean canWarp(@NotNull Bukkit bukkitServer) {
////        return GameType.isTyped(bukkitServer.getName(), SubType.MISC)
////                || bukkitServer.getName().startsWith("ms-");
//    }

}
