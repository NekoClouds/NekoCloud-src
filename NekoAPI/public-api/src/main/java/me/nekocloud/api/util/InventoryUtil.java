package me.nekocloud.api.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class InventoryUtil {

    public void removeItemByMaterial(Inventory inventory, Material material, int amount) {
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null || item.getType() != material) {
                continue;
            }

            ItemStack removeItem = item.clone();
            removeItem.setAmount(amount);
            inventory.removeItem(removeItem);
            return;
        }
    }

    public boolean hasItems(Inventory inventory, Material material, int amount) {
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null || itemStack.getType() != material) {
                continue;
            }

            if (amount <= 0) {
                return true;
            }

            amount -= itemStack.getAmount();
        }

        return amount <= 0;
    }

    /**
     * можно ли добавить вещь в инв
     * @param inventory - инвентарь
     * @return - да или нет
     */
    public boolean canAdd(PlayerInventory inventory, ItemStack itemStack) {
        int empty = 36; //кол-во свободных слотов

        List<ItemStack> armor = Arrays.asList(inventory.getArmorContents());
        for (ItemStack content : inventory.getStorageContents()) {
            if (content == null || armor.contains(content) || content.getType() == Material.AIR) {
                continue;
            }

            if (content.isSimilar(itemStack) &&
                    itemStack.getAmount() + content.getAmount() <= content.getMaxStackSize()) {
                continue;
            }

            empty--;
        }
        return empty > 0;
    }

    public int getEmptySlot(Inventory inventory) {
        for (int i = 0; i < 4; i++) {
            for (int j = 10; j <= 16; j++) {
                int slot = j + i * 9;
                if (inventory.getItem(slot) == null) {
                    return slot;
                }
            }
        }

        return 0;
    }

    public int getPagesCount(int elem, int elemPage) {
        return (elem + elemPage - 1) / elemPage;
    }

    public int getSlotByXY(int x, int y) {
        //x - столбец
        //y - строчка
        return 9 * y + x - 10;

    }

}
