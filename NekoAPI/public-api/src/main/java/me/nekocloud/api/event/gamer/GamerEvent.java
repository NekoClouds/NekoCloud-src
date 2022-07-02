package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.event.DEvent;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

@Getter
public abstract class GamerEvent extends DEvent {

    private final BukkitGamer gamer;

    protected GamerEvent(BukkitGamer gamer) {
        this(gamer, false);
    }


    protected GamerEvent(BukkitGamer gamer, boolean async) {
        super(async);
        this.gamer = gamer;
    }
}
