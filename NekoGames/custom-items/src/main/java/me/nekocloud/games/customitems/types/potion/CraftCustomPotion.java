package me.nekocloud.games.customitems.types.potion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class CraftCustomPotion implements CustomItem {

    private final ItemStack item;

    @Override
    public CustomItemType getType() {
        return CustomItemType.POTION;
    }
}
