package me.nekocloud.api.event.gamer;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.event.Cancellable;

@Getter
public class GamerChangeHiderStateEvent extends GamerEvent implements Cancellable {

    private final boolean hider;

    @Setter
    private boolean cancelled;

    public GamerChangeHiderStateEvent(BukkitGamer gamer, boolean hider) {
        super(gamer);
        this.hider = hider;
    }
}
