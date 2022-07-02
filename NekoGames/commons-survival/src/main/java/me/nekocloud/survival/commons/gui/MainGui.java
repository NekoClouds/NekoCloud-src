package me.nekocloud.survival.commons.gui;

import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.gui.time.PTimeGui;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainGui extends CommonsSurvivalGui<DInventory> {

    public MainGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createInventory(player, lang.getMessage("ALTERNATE_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        User user = USER_MANAGER.getUser(player);
        if (user == null || gamer == null)
            return;

        ItemStack home;
        if (configData.isHomeSystem()) {
            int homeSize = user.getHomes().size();
            home = ItemUtil.getBuilder(Material.BED)
                    .setName("§a" + lang.getMessage("HOME_GUI_NAME"))
                    .setLore(lang.getList("HOME_GUI_LORE",
                            String.valueOf(homeSize), CommonWords.HOMES_2.convert(homeSize, lang)))
                    .build();
            dInventory.setItem(10, new DItem(home, (player, clickType, i) -> {
                MANAGER.getGui(HomeGui.class, player).open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            home = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("HOME_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build();
            dInventory.setItem(10, new DItem(home));
        }

        ItemStack kits;
        if (configData.isKitSystem()) {
            kits = ItemUtil.getBuilder(Material.IRON_SWORD)
                    .setName("§a" + lang.getMessage("KIT_GUI_NAME"))
                    .setLore(lang.getList( "KIT_GUI_LORE"))
                    .build();
            dInventory.setItem(13, new DItem(kits, (player, clickType, i) -> {
                MANAGER.getGui(KitGui.class, player).open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            kits = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("KIT_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build();
            dInventory.setItem(13, new DItem(kits));
        }

        if (configData.isWarpSystem()) {
            int sizeWarps = CommonsSurvivalAPI.getWarpManager().getWarps().size();
            dInventory.setItem(16, new DItem(ItemUtil.getBuilder(Material.SIGN)
                    .setName("§a" + lang.getMessage("WARP_GUI_NAME"))
                    .setLore(lang.getList("WARP_GUI_LORE", String.valueOf(sizeWarps)))
                    .build(), (player, clickType, i) -> {
                CommonsSurvivalGui gui = MANAGER.getGui(MyWarpGui.class, player);

                if (clickType.isLeftClick())
                    gui = MANAGER.getGui(WarpGui.class, player);

                if (gui == null)
                    return;

                gui.open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            dInventory.setItem(16, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("WARP_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build()));
        }

        if (configData.isCallSystem()) {
            int requests = ((CraftUser)(user)).getCallReguests().size();
            dInventory.setItem(9 * 3 + 3, new DItem(ItemUtil.getBuilder(Material.PAPER)
                    .setName("§a" + lang.getMessage("CALL_GUI_NAME"))
                    .setLore(lang.getList("CALL_GUI_LORE",
                            String.valueOf(requests),
                            CommonWords.PLAYERS_1.convert(requests, lang)))
                    .build(), (player, clickType, i) -> {
                TpacceptGui tpacceptGui = MANAGER.getGui(TpacceptGui.class, player);
                if (tpacceptGui != null)
                    tpacceptGui.open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            dInventory.setItem(9 * 3 + 3, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("CALL_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build()));
        }

        dInventory.setItem(9 * 3 + 5, new DItem(ItemUtil.getBuilder(Material.DOUBLE_PLANT)
                .setName("§a" + lang.getMessage("PTIME_GUI_NAME"))
                .setLore(lang.getList( "PTIME_ITEM_LORE",
                        Group.getGroupByLevel(configData.getInt("ptimeCommand")).getNameEn()))
                .build(), (player, clickType, i) -> MANAGER.getGui(PTimeGui.class, player).open()));

        if (!configData.isCallSystem())
            return;
        ItemStack itemStack;
        if (user.isTpToggle()) {
            itemStack = ItemUtil.getBuilder(Material.INK_SACK)
                    .setDurability((short) 10)
                    .setName(lang.getMessage("TPTOGGLE_ITEM_NAME",
                            lang.getMessage("ENABLED")))
                    .setLore(lang.getList("ITEMS_LOBBY_ENABLE_LORE"))
                    .build();
        } else {
            itemStack = ItemUtil.getBuilder(Material.INK_SACK)
                    .setDurability((short) 8)
                    .setName(lang.getMessage("TPTOGGLE_ITEM_NAME", "§c"
                            + lang.getMessage("DISABLED")))
                    .setLore(lang.getList("ITEMS_LOBBY_DISABLE_LORE"))
                    .build();
        }
        dInventory.setItem(44, new DItem(itemStack,
                (player, clickType, slot1) -> player.chat("/tptoggle")));
    }
}
