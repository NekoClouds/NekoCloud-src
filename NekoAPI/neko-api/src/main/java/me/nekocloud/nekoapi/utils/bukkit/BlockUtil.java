package me.nekocloud.nekoapi.utils.bukkit;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class BlockUtil {

    public static ItemStack itemStackFromString(String string) {
        String[] data = string.split(" ");

        ItemStack itemStack;
        Material material;
        short durability;
        int amount = 1;
        if (data[0].contains(":")) {
            String[] materialData = data[0].split(":");
            material = Material.getMaterial(materialData[0]);
            durability = Short.parseShort(materialData[1]);
        } else {
            material = Material.getMaterial(data[0]);
            durability = 0;
        }

        if (data.length > 1) {
            amount = Integer.parseInt(data[1]);
        }

        itemStack = new ItemStack(material, amount, durability);

        if (data.length > 2) {
            for (int i = 2; i < data.length; i++) {
                if (data[i].startsWith("COLOR")) {
                    LeatherArmorMeta lam = (LeatherArmorMeta) itemStack.getItemMeta();
                    data[i] = data[i].replace("COLOR:", "");
                    String[] colors = data[i].split(",");
                    Color color = Color.fromRGB(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
                    lam.setColor(color);
                    itemStack.setItemMeta(lam);
                } else {
                    String[] enchant = data[i].split(":");
                    Enchantment enchantment = Enchantment.getByName(enchant[0]);
                    int level = (enchant.length > 1 ? Integer.parseInt(enchant[1]) : 1);
                    itemStack.addUnsafeEnchantment(enchantment, level);
                }
            }
        }

        return itemStack;
    }
}
