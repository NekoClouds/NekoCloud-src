package me.nekocloud.packetlib.packetreader.event;

import lombok.Getter;
import me.nekocloud.api.event.player.PlayerEvent;
import org.bukkit.entity.Player;

@Getter
public class AsyncChunkSendEvent extends PlayerEvent {

    private final String worldName;
    private final int x;
    private final int z;

    public AsyncChunkSendEvent(Player player, String worldName, int x, int z) {
        super(player, true);
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }
}