package me.nekocloud.games.customitems.types.arrow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import me.nekocloud.games.customitems.api.arrow.CustomArrowEffect;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CraftCustomArrow implements CustomItem {

    private final ItemStack item;
    private final List<CustomArrowEffect> arrowEffects;

    @Override
    public CustomItemType getType() {
        return CustomItemType.ARROW;
    }

    public boolean hasEffect(CustomArrowEffect customArrowEffect) {
        return this.arrowEffects != null && this.arrowEffects.contains(customArrowEffect);
    }
}
