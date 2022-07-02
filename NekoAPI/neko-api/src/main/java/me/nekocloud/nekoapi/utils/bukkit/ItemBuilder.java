package me.nekocloud.nekoapi.utils.bukkit;

import com.google.common.base.Preconditions;
import me.nekocloud.api.util.Head;
import org.apache.commons.lang.StringUtils;
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
import java.util.stream.Collectors;

public class ItemBuilder implements Builder<ItemStack> {

    private final ItemStack itemStack;

    public static Material getMaterialById(int id) {
        return Material.getMaterial(id);
    }

    public static ItemBuilder builder(String id) {
        return new ItemBuilder(id);
    }

    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder builder(ItemStack item) {
        return new ItemBuilder(item);
    }

    private ItemBuilder(String input) {
        String[] args = input.split(":");

        String name = args[0];
        String meta = args.length > 1 ? args[1] : null;
        Material material;
        if (StringUtils.isNumeric(name)) {
            material = getMaterialById(Integer.parseInt(name));
            Preconditions.checkArgument(material != null, "Material id not found: " + input);
        } else {
            name = name.toUpperCase();
            if (name.equals("HEAD")) {
                Preconditions.checkArgument(meta != null, "Skull owner is null: " + input);
                this.itemStack = Head.getHeadByValue(meta);
                return;
            }
            if (name.equals("MONSTER_EGG")) {
                this.itemStack = new ItemStack(Material.MONSTER_EGG);
                SpawnEggMeta spawnEggMeta = (SpawnEggMeta) this.itemStack.getItemMeta();
                spawnEggMeta.setSpawnedType(EntityType.valueOf(meta));
                return;
            }
            material = Material.getMaterial(name);
            Preconditions.checkArgument(material != null, "Material type not found: " + name);
        }
        short damage;
        if (meta == null) {
            damage = 0;
        } else {
            try {
                damage = Short.parseShort(meta);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Material data is not number: " + input);
            }
        }
        this.itemStack = new ItemStack(material, 1, damage);
    }

    private ItemBuilder(ItemStack is) {
        itemStack = is;
    }

    private ItemBuilder(Material material) {
        itemStack = new ItemStack(material, 1);
    }

    private ItemBuilder(Material material, ItemMeta im) {
        itemStack = new ItemStack(material, 1);
        itemStack.setItemMeta(im);
    }

    public ItemBuilder setPotionData(PotionData data) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).setBasePotionData(data);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setPotionData(PotionType type) {
        return setPotionData(type, false, false);
    }

    public ItemBuilder setEggType(EntityType entityType) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof SpawnEggMeta) {
            ((SpawnEggMeta) meta).setSpawnedType(entityType);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setPotionData(PotionType type, boolean extended, boolean upgraded) {
        return setPotionData(new PotionData(type, extended, upgraded));
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(Math.min(amount, 64));
        return this;
    }

    public ItemBuilder removeFlags() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Arrays.stream(ItemFlag.values()).forEach(itemMeta::addItemFlags);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder removeEnchantment() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getEnchants().keySet().forEach(itemMeta::removeEnchant);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        return setUnbreakable(true);
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);
        addFlag(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder glowing(){
        addFlag(ItemFlag.HIDE_ENCHANTS);
        this.itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        return this;
    }

    public ItemBuilder glowing(boolean glow){
        if(!glow) {
            this.itemStack.getEnchantments().forEach((enchant, level) -> itemStack.removeEnchantment(enchant));
            return this;
        }
        addFlag(ItemFlag.HIDE_ENCHANTS);
        this.itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment type, int level) {
        itemStack.addUnsafeEnchantment(type, level);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setData(MaterialData materialData) {
        itemStack.setData(materialData);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(name);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(new ArrayList<>(lore));
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(List<String> lore) {
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

    public ItemBuilder replaceLore(String in, String out) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = im.getLore();
        if (lore != null) {
            im.setLore(lore.stream()
                    .map(line -> line.replace(in, out))
                    .collect(Collectors.toList()));
            itemStack.setItemMeta(im);
        }
        return this;
    }

    public ItemBuilder replaceLore(String in, List<String> out) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = im.getLore();
        List<String> newLore = new ArrayList<>();
        if (lore != null) {
            for (String line : lore) {
                if (line.startsWith(in))
                    newLore.addAll(out);
                else
                    newLore.add(line);
            }
            im.setLore(newLore);
            itemStack.setItemMeta(im);
        }
        return this;
    }

    public ItemBuilder addFlag(ItemFlag... flag) {
        ItemMeta im = itemStack.getItemMeta();
        im.addItemFlags(flag);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        return addFlag(new ItemFlag[] { flag });
    }

    public ItemBuilder setColor(DyeColor color) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Colorable) {
            ((Colorable) itemMeta).setColor(color);
            itemStack.setItemMeta(itemMeta);
        } else {
            // noinspection deprecation
            itemStack.setDurability(color.getWoolData());
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta) ((LeatherArmorMeta) itemMeta).setColor(color);
        else if (itemMeta instanceof PotionMeta) ((PotionMeta) itemMeta).setColor(color);
        else return this.setColor(DyeColor.getByColor(color));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addCustomEffect(PotionEffect effect, boolean overwrite) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).addCustomEffect(effect, overwrite);
            itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    @Override
    public ItemStack build() {
        return itemStack;
    }
}
