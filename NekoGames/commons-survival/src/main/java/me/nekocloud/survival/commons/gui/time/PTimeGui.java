package me.nekocloud.survival.commons.gui.time;

import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.guis.CustomItems;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

public class PTimeGui extends CommonsSurvivalGui<DInventory> {

    public PTimeGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer bukkitGamer = GAMER_MANAGER.getGamer(player);
        if (bukkitGamer == null)
            return;

        lang = bukkitGamer.getLanguage();
        dInventory = INVENTORY_API.createInventory(player, lang.getMessage("ALTERNATE_GUI_NAME")
                + " ▸ " + lang.getMessage("PTIME_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        dInventory.setItem(40, new DItem(CustomItems.getBack(lang), (player, clickType, i) -> {
            SOUND_API.play(player, SoundType.PICKUP);
            player.chat("/menu");
        }));

        int level = configData.getInt("ptimeCommand");
        dInventory.setItem(11, new DItem(ItemUtil.getBuilder(Material.WATCH)
                .setName(lang.getMessage( "PTIME_ITEM_WATCH_NAME"))
                .setLore(lang.getList("PTIME_ITEM_WATCH_LORE",
                        lang.getMessage( "MORNING"),
                        Group.getGroupByLevel(level).getNameEn()))
                .build(), (player, clickType, i) -> {
                    if (gamer.getGroup().getLevel() >= level) {
                        player.setPlayerTime(0, false);
                        player.closeInventory();
                    }
                }));

        dInventory.setItem(13, new DItem(ItemUtil.getBuilder(Material.WATCH)
                .setName(lang.getMessage( "PTIME_ITEM_WATCH_NAME"))
                .setLore(lang.getList("PTIME_ITEM_WATCH_LORE",
                        lang.getMessage( "DAY"),
                        Group.getGroupByLevel(level).getNameEn()))
                .build(), (player, clickType, i) -> {
            if (gamer.getGroup().getLevel() >= level) {
                player.setPlayerTime(6000, false);
                player.closeInventory();
            }
        }));

        dInventory.setItem(15, new DItem(ItemUtil.getBuilder(Material.WATCH)
                        .setName(lang.getMessage( "PTIME_ITEM_WATCH_NAME"))
                        .setLore(lang.getList("PTIME_ITEM_WATCH_LORE",
                                lang.getMessage( "NIGHT"),
                                Group.getGroupByLevel(level).getNameEn()))
                        .build(), (player, clickType, i) -> {
            if (gamer.getGroup().getLevel() >= level) {
                player.setPlayerTime(18000, false);
                player.closeInventory();
            }
        }));

        dInventory.setItem(3 * 9 + 3, new DItem(ItemUtil.getBuilder(Material.DOUBLE_PLANT)
                .setName(lang.getMessage( "PTIME_ITEM_WEATHER_NAME"))
                .setLore(lang.getList( "PTIME_ITEM_WEATHER_LORE",
                        lang.getMessage( "SUNNY"),
                        Group.getGroupByLevel(level).getNameEn()))
                .build(), (player, clickType, i) -> {
            if (gamer.getGroup().getLevel() >= level) {
                player.setPlayerWeather(WeatherType.CLEAR);
                player.sendMessage(configData.getPrefix() + lang.getMessage("PTIME_WEATHER_CHANGED"));
                player.closeInventory();
            }
        }));

        dInventory.setItem(3 * 9 + 5, new DItem(ItemUtil.getBuilder(Material.WATER_BUCKET)
                .setName(lang.getMessage("PTIME_ITEM_WEATHER_NAME"))
                .setLore(lang.getList("PTIME_ITEM_WEATHER_LORE",
                        lang.getMessage("RAINY"),
                        Group.getGroupByLevel(level).getNameEn()))
                .build(), (player, clickType, i) -> {
            if (gamer.getGroup().getLevel() >= level) {
                player.setPlayerWeather(WeatherType.DOWNFALL);
                player.sendMessage(configData.getPrefix() + lang.getMessage("PTIME_WEATHER_CHANGED"));
                player.closeInventory();
            }
        }));

        dInventory.setItem(44, new DItem(ItemUtil.getBuilder(Material.BUCKET)
                .setName(lang.getMessage("PTIME_ITEM_RESET_NAME"))
                .setLore(lang.getList("PTIME_ITEM_RESET_LORE"))
                .build(), (player, clickType, i) -> {
            player.chat("/ptime reset");
            player.closeInventory();
        }));
    }
}
