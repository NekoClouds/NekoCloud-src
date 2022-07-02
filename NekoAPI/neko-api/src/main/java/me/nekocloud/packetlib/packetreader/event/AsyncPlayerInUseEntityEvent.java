package me.nekocloud.packetlib.packetreader.event;

import lombok.Getter;
import me.nekocloud.api.event.player.PlayerEvent;
import org.bukkit.entity.Player;

@Getter
public class AsyncPlayerInUseEntityEvent extends PlayerEvent {

    private final int entityId;
    private final Action action;
    private final Hand hand;

    public AsyncPlayerInUseEntityEvent(Player player, int entityId, Action action, Hand hand) {
        super(player, true);
        this.entityId = entityId;
        this.action = action;
        this.hand = hand;
    }

    public enum Action {
        ATTACK, INTERACT_AT, INTERACT
    }

    public enum Hand {
        OFF_HAND, MAIN_HAND
    }
}
