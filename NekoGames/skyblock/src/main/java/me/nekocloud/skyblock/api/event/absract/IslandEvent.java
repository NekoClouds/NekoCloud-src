package me.nekocloud.skyblock.api.event.absract;

import lombok.Getter;
import me.nekocloud.api.event.DEvent;
import me.nekocloud.skyblock.api.island.Island;

@Getter
public abstract class IslandEvent extends DEvent {

    private final Island island;

    protected IslandEvent(Island island, boolean async) {
        super(async);
        this.island = island;
    }

    protected IslandEvent(Island island) {
        this.island = island;
    }
}
