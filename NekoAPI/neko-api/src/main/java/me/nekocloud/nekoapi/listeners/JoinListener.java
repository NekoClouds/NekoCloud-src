package me.nekocloud.nekoapi.listeners;

import lombok.val;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.base.gamer.constans.JoinMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class JoinListener extends DListener<JavaPlugin> {

    public JoinListener(final JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler
    public void onJoin(final @NotNull AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();

        if (gamer.getName().equalsIgnoreCase("_Novit_")) {
            return;
        }

        val joinMessage = gamer.getJoinMessage();
        if (joinMessage == null)
            return;

        if (joinMessage == JoinMessage.DEFAULT_MESSAGE) {
            GAMER_MANAGER.getGamerEntities().values().forEach(gamerEntity ->
                    gamerEntity.sendMessageLocale("JOIN_PLAYER_LO_LOBBY", gamer.getChatName()));
            return;
        }

        GAMER_MANAGER.getGamerEntities().values().forEach(gamerEntity ->
                gamerEntity.sendMessage(String.format(joinMessage.getMessage(), gamer.getChatName())));
    }
}
