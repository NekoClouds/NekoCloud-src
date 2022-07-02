package me.nekocloud.skyblock.api.event.module;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.skyblock.api.event.absract.IslandEvent;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.IslandFlag;
import org.bukkit.event.Cancellable;

@Getter
public class IslandSetFlagEvent extends IslandEvent implements Cancellable {

    private final IslandFlag flag;
    private final boolean result;

    @Setter
    private boolean cancelled;

    public IslandSetFlagEvent(Island island, IslandFlag flag, boolean result) {
        super(island);
        this.flag = flag;
        this.result = result;
    }
}
