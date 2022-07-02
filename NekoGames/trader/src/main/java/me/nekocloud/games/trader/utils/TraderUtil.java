package me.nekocloud.games.trader.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

@UtilityClass
public class TraderUtil {

    public static int getItemsAmount(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();

        if (!inventory.contains(itemStack.getType())) {
            return 0;
        } else {
            int amount = 0;

            for (ItemStack content : inventory.getContents()) {
                if (content != null && (inventory.getItemInOffHand() == null || inventory.getItemInOffHand().getType() != content.getType()) && (inventory.getHelmet() == null || inventory.getHelmet().getType() != content.getType()) && content.getType() == itemStack.getType() && content.getDurability() == itemStack.getDurability()) {
                    ItemMeta meta = content.getItemMeta();
                    if (meta == null || meta.getDisplayName() == null) {
                        amount += content.getAmount();
                    }
                }
            }

            return amount;
        }
    }

    public static void removeItems(Player player, ItemStack itemStack, int amount) {
        PlayerInventory inventory = player.getInventory();

        for (ItemStack content : inventory.getContents()) {
            if (amount <= 0) {
                break;
            }

            if (content != null && content.getType() != Material.AIR && (inventory.getItemInOffHand() == null || inventory.getItemInOffHand().getType() != itemStack.getType()) && (inventory.getHelmet() == null || inventory.getHelmet().getType() != itemStack.getType()) && content.getType() == itemStack.getType()) {
                ItemStack removeItem = content.clone();
                if (removeItem.getAmount() >= amount) {
                    removeItem.setAmount(amount);
                }

                inventory.removeItem(removeItem);
                amount -= removeItem.getAmount();
            }
        }
    }
}
