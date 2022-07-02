package me.nekocloud.skyblock.api.event.module;

import me.nekocloud.skyblock.api.event.absract.IslandEvent;
import me.nekocloud.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;

public class IslandUpgradeEvent extends IslandEvent implements Cancellable {

    private boolean cancel;

    public IslandUpgradeEvent(Island island) {
        super(island);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.cancel = flag;
    }
}
