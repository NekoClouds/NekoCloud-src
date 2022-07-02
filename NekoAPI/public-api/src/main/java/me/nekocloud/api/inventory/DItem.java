package me.nekocloud.api.inventory;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.inventory.action.ClickAction;
import org.bukkit.inventory.ItemStack;

@Setter
@Getter
public class DItem {

    private ItemStack item;
    private ClickAction clickAction;

    public DItem(ItemStack itemStack, ClickAction clickAction) {
        this.item = itemStack;
        this.clickAction = clickAction;
    }

    public DItem(ItemStack itemStack) {
        this(itemStack, (player, clickType, slot) -> {
            //нихуя
        });
    }
}
