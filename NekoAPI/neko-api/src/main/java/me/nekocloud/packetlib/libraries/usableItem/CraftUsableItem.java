package me.nekocloud.packetlib.libraries.usableItem;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.usableitem.ClickAction;
import me.nekocloud.api.usableitem.UsableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class CraftUsableItem implements UsableItem {

    private final UsablesManager manager;

    @Setter
    private Player owner;
    private ItemStack itemStack;
    private String displayName;
    @Setter
    private boolean drop = false;
    @Setter
    private boolean invClick = true;
    private ClickAction clickAction;

    CraftUsableItem(ItemStack itemStack, Player owner, ClickAction action, UsablesManager manager) {
        this.manager = manager;
        this.clickAction = action;
        this.itemStack = itemStack;
        this.owner = owner;
        this.displayName = itemStack.getItemMeta().getDisplayName();

        manager.getUsableItems().put(displayName, this);
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @Override
    public void remove() {
        manager.getUsableItems().remove(this.displayName);
    }
}
