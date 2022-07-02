package me.nekocloud.api.event.gamer.async;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.event.Cancellable;

@Getter @Setter
public class AsyncGamerJoinEvent extends GamerEvent implements Cancellable {

    private boolean cancelled;

    public AsyncGamerJoinEvent(BukkitGamer gamer) {
        super(gamer, true);
    }
}
