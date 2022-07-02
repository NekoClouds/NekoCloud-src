package me.nekocloud.vanish.api.events;

import me.nekocloud.api.event.player.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerHideEvent extends PlayerEvent {

    protected PlayerHideEvent(Player player) {
        super(player);
    }
}
