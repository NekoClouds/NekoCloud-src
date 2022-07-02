package me.nekocloud.api.event.player;

import lombok.Getter;
import me.nekocloud.api.event.DEvent;
import org.bukkit.entity.Player;

@Getter
public abstract class PlayerEvent extends DEvent {

    private final Player player;

    protected PlayerEvent(Player player) {
        this(player, false);
    }

    protected PlayerEvent(Player player, boolean async) {
        super(async);
        this.player = player;
    }
}
