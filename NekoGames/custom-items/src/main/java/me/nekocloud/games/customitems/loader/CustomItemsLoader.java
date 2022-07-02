package me.nekocloud.games.customitems.loader;

import lombok.experimental.UtilityClass;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.games.customitems.CustomItems;
import me.nekocloud.games.customitems.api.CustomItemType;
import me.nekocloud.games.customitems.api.CustomItemsAPI;
import me.nekocloud.games.customitems.api.arrow.CustomArrowEffect;
import me.nekocloud.games.customitems.manager.CustomItemsManager;
import me.nekocloud.games.customitems.types.arrow.CraftCustomArrow;
import me.nekocloud.games.customitems.types.axe.CraftThrowingAxe;
import me.nekocloud.games.customitems.types.axe.listener.ThrowingAxeListener;
import me.nekocloud.games.customitems.types.feather.CraftFeather;
import me.nekocloud.games.customitems.types.feather.CraftFeatherUp;
import me.nekocloud.games.customitems.types.feather.listener.FeatherListener;
import me.nekocloud.games.customitems.types.potion.CraftCustomPotion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CustomItemsLoader {

    public void loadItems(CustomItems plugin) {
        FileConfiguration config = plugin.getConfig();
        CustomItemsManager manager = CustomItemsAPI.getItemsManager();
        ConfigurationSection section = config.getConfigurationSection("items");
        for (String name : section.getValues(false).keySet()) {
            String typeName = section.getString(name + ".type");
            CustomItemType type = CustomItemType.getByName(section.getString(name + ".type"));
            if (type == null) {
                throw new IllegalArgumentException("CustomItemType " + typeName + " not found");
            }
            ItemUtil.Builder itemBuilder = ItemUtil.getBuilder(Material.getMaterial(section.getString(name + ".id")))
                    .setName(section.getString(name + ".name"))
                    .setLore(section.getStringList(name + ".lore"));

            if (section.contains(name + ".enchantments")) {
                section.getStringList(name + ".enchantments").forEach(enchantment -> {
                    String[] splitted = enchantment.split(":");
                    itemBuilder.addEnchantment(Enchantment.getByName(splitted[0].toUpperCase()),
                            Integer.parseInt(splitted[1]));
                });
            }
            switch (type) {
                case THROW_AXE: {
                    manager.addItem(name, new CraftThrowingAxe(itemBuilder.build(),
                            section.getDouble(name + ".damage")));
                    break;
                }
                case POTION: {
                    parsePotionEffects(section.getStringList(name + ".effects"))
                            .forEach(potionEffect -> itemBuilder.addCustomEffect(potionEffect, true));
                    setPotionColor(itemBuilder, section, name);
                    manager.addItem(name, new CraftCustomPotion(itemBuilder.build()));
                    break;
                }
                case ARROW: {
                    parsePotionEffects(section.getStringList(name + ".effects"))
                            .forEach(potionEffect -> itemBuilder.addCustomEffect(potionEffect, true));
                    setPotionColor(itemBuilder, section, name);
                    List<CustomArrowEffect> arrowEffects =
                            section.getStringList(name + ".arrow_effects").stream()
                                    .map(CustomArrowEffect::getByName)
                                    .collect(Collectors.toList());
                    manager.addItem(name, new CraftCustomArrow(itemBuilder.build(), arrowEffects));
                    break;
                }
                case FEATHER: {
                    manager.addItem(name, new CraftFeather(itemBuilder.build(),
                            section.getDouble(name + ".multiple_y")));
                }
                case FEATHER_UP: {
                    manager.addItem(name, new CraftFeatherUp(itemBuilder.build()));
                }
            }
        }
        initItemsFunctional(plugin, manager);
    }

    //переписать под lazy init просто
    private void initItemsFunctional(CustomItems plugin, CustomItemsManager manager) {
        if (manager.hasItem(CustomItemType.ARROW))
            //new ArrowListener(plugin);

        if (manager.hasItem(CustomItemType.THROW_AXE))
            new ThrowingAxeListener(plugin);

        if (manager.hasItem(CustomItemType.FEATHER) || manager.hasItem(CustomItemType.FEATHER_UP))
            new FeatherListener(plugin);
    }

    private void setPotionColor(ItemUtil.Builder itemBuilder, ConfigurationSection section, String name) {
        if (section.contains(name + ".color")) {
            String[] rgb = section.getString(name + ".color").split(",");
            itemBuilder.setColor(Color.fromRGB(
                    Integer.parseInt(rgb[0]),
                    Integer.parseInt(rgb[1]),
                    Integer.parseInt(rgb[2])));
        }
    }

    private List<PotionEffect> parsePotionEffects(List<String> potionEffects) {
        List<PotionEffect> potions = new ArrayList<>();
        for (String potionEffect : potionEffects) {
            String[] splitted = potionEffect.split(";");
            PotionEffectType type = PotionEffectType.getByName(splitted[0].toUpperCase());
            if (type == null)
                throw new NullPointerException("PotionEffectType " + splitted[0] + " not found");
            potions.add(new PotionEffect(type, Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2])));
        }
        return potions;
    }
}
