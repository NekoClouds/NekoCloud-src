package me.nekocloud.skyblock.api.event.module;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.skyblock.api.event.absract.IslandEvent;
import me.nekocloud.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class IslandUpgradeGenerator extends IslandEvent implements Cancellable {

    private boolean cancelled;

    public IslandUpgradeGenerator(Island island) {
        super(island);
    }
}
