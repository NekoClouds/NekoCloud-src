package me.nekocloud.api.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ItemUtil {

    private final IntList RESTRICTED_FLOOR_BLOCKS = new IntArrayList(new int[] {
            6, 111, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232,
            233, 234, 106, 171, 175, 65, 38, 32, 11, 12, 15, 26, 27, 28, 29, 39, 34, 36, 37, 39, 40,
            50, 51, 52, 55, 56, 59, 64, 69, 71, 73, 143, 74, 75, 76, 77, 81, 90, 122, 150, 355, 138,
            66, 157, 397}
    );

    public short getBedColor(ChatColor chatColor) {
        return switch (chatColor) {
            case BLACK -> 15;
            case DARK_BLUE -> 11;
            case DARK_GREEN -> 13;
            case DARK_AQUA -> 9;
            case DARK_PURPLE -> 10;
            case GOLD -> 1;
            case GRAY -> 8;
            case DARK_GRAY -> 7;
            case AQUA, BLUE -> 3;
            case GREEN -> 5;
            case LIGHT_PURPLE -> 2;
            case YELLOW -> 4;
            case WHITE -> 0;
            default -> 14; //DARK_RED | RED
        };
    }

    public Builder getBuilder(ItemStack item) {
        return new Builder(item);
    }

    public Builder getBuilder(Material material) {
        return new Builder(material);
    }

    public Builder getBuilder(Head head) {
        return new Builder(head.getHead());
    }

    public Builder getBuilder(Material material, ItemMeta itemMeta) {
        return new Builder(material, itemMeta);
    }

    public static class Builder {

        private final ItemStack itemStack;

        private Builder(ItemStack is) {
            itemStack = is;
        }

        private Builder(Material material) {
            itemStack = new ItemStack(material, 1);
        }

        private Builder(Material material, ItemMeta im) {
            itemStack = new ItemStack(material, 1);
            itemStack.setItemMeta(im);
        }

        public Builder setPotionData(PotionData data) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof PotionMeta) {
                ((PotionMeta) meta).setBasePotionData(data);
                itemStack.setItemMeta(meta);
            }
            return this;
        }

        public Builder setPotionData(PotionType type) {
            return setPotionData(type, false, false);
        }

        public Builder setEggType(EntityType entityType) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof SpawnEggMeta) {
                ((SpawnEggMeta) meta).setSpawnedType(entityType);
                itemStack.setItemMeta(meta);
            }
            return this;
        }

        public Builder setPotionData(PotionType type, boolean extended, boolean upgraded) {
            return setPotionData(new PotionData(type, extended, upgraded));
        }

        public Builder setAmount(int amount) {
            if (amount > 64) {
                amount = 64;
            }
            itemStack.setAmount(amount);
            return this;
        }

        public Builder removeFlags() {
            ItemMeta itemMeta = itemStack.getItemMeta();
            Arrays.stream(ItemFlag.values()).forEach(itemMeta::addItemFlags);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public Builder removeEnchantment() {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.getEnchants().keySet().forEach(itemMeta::removeEnchant);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public Builder setUnbreakable() {
            return setUnbreakable(true);
        }

        public Builder setUnbreakable(boolean unbreakable) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setUnbreakable(unbreakable);
            itemStack.setItemMeta(itemMeta);
            addFlag(ItemFlag.HIDE_UNBREAKABLE);
            return this;
        }

        public Builder glowing(){
            addFlag(ItemFlag.HIDE_ENCHANTS);
            this.itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
            return this;
        }

        public Builder glowing(boolean glow){
            if(!glow) {
                this.itemStack.getEnchantments().forEach((enchant, level) -> itemStack.removeEnchantment(enchant));
                return this;
            }
            addFlag(ItemFlag.HIDE_ENCHANTS);
            this.itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
            return this;
        }

        public Builder addEnchantment(Enchantment type, int level) {
            itemStack.addUnsafeEnchantment(type, level);
            return this;
        }

        public Builder setDurability(short durability) {
            itemStack.setDurability(durability);
            return this;
        }

        public Builder setData(MaterialData materialData) {
            itemStack.setData(materialData);
            return this;
        }

        public Builder setName(String name) {
            ItemMeta im = itemStack.getItemMeta();
            im.setDisplayName(name);
            itemStack.setItemMeta(im);
            return this;
        }

        public Builder setLore(String... lore) {
            return setLore(Arrays.asList(lore));
        }

        public Builder setLore(List<String> lore) {
            ItemMeta im = itemStack.getItemMeta();
            im.setLore(new ArrayList<>(lore));
            itemStack.setItemMeta(im);
            return this;
        }

        public Builder addLore(String... lore) {
            return addLore(Arrays.asList(lore));
        }

        public Builder addLore(List<String> lore) {
            ItemMeta im = itemStack.getItemMeta();
            List<String> oldLore = im.getLore();
            if (oldLore != null) {
                oldLore.addAll(lore);
            } else {
                oldLore = lore;
            }
            im.setLore(oldLore);
            itemStack.setItemMeta(im);
            return this;
        }

        public Builder addFlag(ItemFlag... flag) {
            ItemMeta im = itemStack.getItemMeta();
            im.addItemFlags(flag);
            itemStack.setItemMeta(im);
            return this;
        }

        public Builder addFlag(ItemFlag flag) {
            return addFlag(new ItemFlag[] { flag });
        }

        public Builder setColor(DyeColor color) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof Colorable) {
                ((Colorable) itemMeta).setColor(color);
                itemStack.setItemMeta(itemMeta);
            }
            return this;
        }

        public Builder setColor(Color color) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) itemMeta).setColor(color);
                itemStack.setItemMeta(itemMeta);
            } else if (itemMeta instanceof PotionMeta) {
                ((PotionMeta) itemMeta).setColor(color);
                itemStack.setItemMeta(itemMeta);
            }
            return this;
        }

        public Builder addCustomEffect(PotionEffect effect, boolean overwrite) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof PotionMeta) {
                ((PotionMeta) itemMeta).addCustomEffect(effect, overwrite);
                itemStack.setItemMeta(itemMeta);
            }
            return this;
        }

        public ItemStack build() {
            return itemStack;
        }
    }

    public boolean isRestricted(Material material) {
        return RESTRICTED_FLOOR_BLOCKS.contains(material.getId());
    }

}
