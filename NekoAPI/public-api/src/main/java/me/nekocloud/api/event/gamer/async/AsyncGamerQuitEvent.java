package me.nekocloud.api.event.gamer.async;

import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;

public class AsyncGamerQuitEvent extends GamerEvent {

    public AsyncGamerQuitEvent(BukkitGamer gamer) {
        super(gamer, true);
    }
}
