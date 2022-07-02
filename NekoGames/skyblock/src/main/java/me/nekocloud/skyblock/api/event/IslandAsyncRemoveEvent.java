package me.nekocloud.skyblock.api.event;

import me.nekocloud.skyblock.api.event.absract.IslandEvent;
import me.nekocloud.skyblock.api.island.Island;

public class IslandAsyncRemoveEvent extends IslandEvent {

    public IslandAsyncRemoveEvent(Island island) {
        super(island, true);
    }
}
