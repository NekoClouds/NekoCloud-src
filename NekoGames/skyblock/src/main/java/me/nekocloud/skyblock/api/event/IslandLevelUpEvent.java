package me.nekocloud.skyblock.api.event;

import lombok.Getter;
import me.nekocloud.skyblock.api.event.absract.IslandEvent;
import me.nekocloud.skyblock.api.island.Island;

public class IslandLevelUpEvent extends IslandEvent {

    @Getter
    private final int level;

    protected IslandLevelUpEvent(Island island, int level) {
        super(island);
        this.level = level;
    }
}
