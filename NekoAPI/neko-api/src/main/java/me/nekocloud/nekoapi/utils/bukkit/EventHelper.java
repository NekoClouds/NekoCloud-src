package me.nekocloud.nekoapi.utils.bukkit;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class EventHelper {

    public boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    public boolean isLeftClick(Action action) {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    }

    public int getCraftAmount(CraftItemEvent e) {
        Player player = (Player) e.getWhoClicked();
        CraftingInventory inventory = e.getInventory();
        ItemStack craftItemStack = e.getRecipe().getResult();

        if (e.isShiftClick()) {
            int itemsChecked = 0;
            int possibleCreations = 1;

            int amountCanBeMade = 0;

            for (ItemStack item : inventory.getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }

            int amountOfItems = craftItemStack.getAmount() * possibleCreations;
            for (int s = 0; s <= inventory.getSize(); s++) {
                ItemStack test = player.getInventory().getItem(s);
                if (test == null || test.getType() == Material.AIR) {
                    amountCanBeMade += craftItemStack.getMaxStackSize();
                    continue;
                }
                if (test.isSimilar(craftItemStack)) {
                    amountCanBeMade += craftItemStack.getMaxStackSize() - test.getAmount();
                }
            }

            return Math.min(amountOfItems, amountCanBeMade);
        } else {
            return craftItemStack.getAmount();
        }
    }
}