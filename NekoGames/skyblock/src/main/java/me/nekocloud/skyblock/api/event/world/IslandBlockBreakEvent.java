package me.nekocloud.skyblock.api.event.world;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.skyblock.api.event.absract.IslandBlockEvent;
import me.nekocloud.skyblock.api.island.Island;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
@Setter
public class IslandBlockBreakEvent extends IslandBlockEvent {

    private boolean dropItems = true;

    public IslandBlockBreakEvent(Island island, Block block, Player player) {
        super(island, block, player);
    }
}
