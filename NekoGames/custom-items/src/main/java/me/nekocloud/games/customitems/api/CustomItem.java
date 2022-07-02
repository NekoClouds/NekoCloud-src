package me.nekocloud.games.customitems.api;

import org.bukkit.inventory.ItemStack;

public interface CustomItem {

    CustomItemType getType();

    ItemStack getItem();
}
