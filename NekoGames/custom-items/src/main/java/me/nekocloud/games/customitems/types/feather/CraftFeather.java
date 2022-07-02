package me.nekocloud.games.customitems.types.feather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class CraftFeather implements CustomItem {

    private final ItemStack item;
    private final double multipleY;

    @Override
    public CustomItemType getType() {
        return CustomItemType.FEATHER;
    }
}
