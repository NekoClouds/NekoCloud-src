package me.nekocloud.core.connection.player.offline;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.AuthCompleteEvent;

import java.util.Collection;
import java.util.function.Supplier;

public class OfflineMessageListener implements EventListener {

    @EventHandler //TODO: переписать
    public void onPlayerAuthComplete(final AuthCompleteEvent event) {
        val corePlayer = event.getCorePlayer();

        final Collection<Supplier<String>> offlineMessageCollection = NekoCore.getInstance().getPlayerManager()
                .getOfflineMessageMap().get(corePlayer.getName().toLowerCase());

        for (val offlineMessageSupplier : offlineMessageCollection) {
            val offlineMessage = offlineMessageSupplier.get();

            if (offlineMessage != null) {
                corePlayer.sendMessageLocale("OFFLINE_MESSAGES_SEND_ON_JOIN");
                corePlayer.sendMessage(offlineMessage);
            }
        }
    }

}
