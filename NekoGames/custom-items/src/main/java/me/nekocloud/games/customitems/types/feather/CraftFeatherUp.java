package me.nekocloud.games.customitems.types.feather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class CraftFeatherUp implements CustomItem {

    @Getter
    private final ItemStack item;

    @Override
    public CustomItemType getType() {
        return CustomItemType.FEATHER_UP;
    }
}
