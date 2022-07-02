package me.nekocloud.games.customitems.types.axe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class CraftThrowingAxe implements CustomItem {

    private final ItemStack item;
    private final double damage;

    public CustomItemType getType() {
        return CustomItemType.THROW_AXE;
    }
}
