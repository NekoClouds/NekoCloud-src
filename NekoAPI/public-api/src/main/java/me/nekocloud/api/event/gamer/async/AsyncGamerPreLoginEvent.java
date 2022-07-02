package me.nekocloud.api.event.gamer.async;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@Getter
public class AsyncGamerPreLoginEvent extends GamerEvent implements Cancellable {

    private final AsyncPlayerPreLoginEvent event;

    @Setter
    private boolean cancelled;

    public AsyncGamerPreLoginEvent(BukkitGamer gamer, AsyncPlayerPreLoginEvent event) {
        super(gamer, true);
        this.event = event;
    }
}
