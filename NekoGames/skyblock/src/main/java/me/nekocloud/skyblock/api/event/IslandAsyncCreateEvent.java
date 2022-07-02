package me.nekocloud.skyblock.api.event;

import lombok.Getter;
import me.nekocloud.api.event.DEvent;
import me.nekocloud.skyblock.api.island.Island;
import org.bukkit.entity.Player;

@Getter
public class IslandAsyncCreateEvent extends DEvent {

    private final Player player;
    private final Island island;

    public IslandAsyncCreateEvent(Player player, Island island) {
        super(true);
        this.island = island;
        this.player = player;
    }
}
