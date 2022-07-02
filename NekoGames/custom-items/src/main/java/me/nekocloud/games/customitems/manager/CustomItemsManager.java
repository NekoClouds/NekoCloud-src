package me.nekocloud.games.customitems.manager;

import lombok.Getter;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItemsManager {

    @Getter
    private final Map<String, CustomItem> items = new HashMap<>();

    public boolean hasItem(CustomItemType itemType) {
        return this.items.values().stream()
                .filter(item -> item.getType() == itemType)
                .findFirst()
                .orElse(null) != null;
    }

    public CustomItem getItem(CustomItemType itemType, ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return null;

        for (CustomItem customItem : this.items.values()) {
            if (customItem.getType() != itemType)
                continue;

            ItemStack itemStack = customItem.getItem();
            if (itemStack.getType() == item.getType() &&
                    itemStack.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()) &&
                    (!itemStack.getItemMeta().hasLore() || itemStack.getItemMeta().getLore().equals(item.getItemMeta().getLore()))) {
                return customItem;
            }
        }
        return null;
    }

    public CustomItem getItem(String name) {
        return this.items.get(name.toLowerCase());
    }

    public void addItem(String name, CustomItem customItem) {
        this.items.put(name.toLowerCase(), customItem);
    }

    public void removeItem(String name) {
        this.items.remove(name.toLowerCase());
    }
}
