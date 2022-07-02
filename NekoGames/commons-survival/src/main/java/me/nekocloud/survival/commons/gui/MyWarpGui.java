package me.nekocloud.survival.commons.gui;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MyWarpGui extends CommonsSurvivalGui<MultiInventory> {

    public MyWarpGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createMultiInventory(lang.getMessage("ALTERNATE_GUI_NAME")
                + " ▸ " + lang.getMessage("WARP_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        List<Warp> warps = CommonsSurvivalAPI.getWarpManager().getWarps(player.getName());
        int size = warps.size();

        dInventory.clearInventories();

        for (DInventory dInventory : dInventory.getInventories()) {
            dInventory.setItem(44, new DItem(ItemUtil.getBuilder(Material.SIGN)
                    .setName("§a" + lang.getMessage("WARP_ITEM_CHANGER_NAME"))
                    .setLore(lang.getList("WARP_ITEM_CHANGER_LORE"))
                    .build(), (player, clickType, i) -> MANAGER.getGui(WarpGui.class, player).open()));
        }

        if (size == 0) {
            dInventory.setItem(0, 22, new DItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                    .setName(lang.getMessage("MYWARP_ITEM_EMPTY_NAME"))
                    .setLore(lang.getList("MYWARP_ITEM_EMPTY_LORE"))
                    .build(), (player, clickType, slot1) -> SOUND_API.play(player, SoundType.TELEPORT)));
            setAction(dInventory, lang, size);
            return;
        }

        int pageNum = 0;
        int slot = 10;
        for (Warp warp : warps) {
            if (dInventory.getInventories().size() == 0)
                continue;

            ItemStack itemWarp;
            if (warp.isPrivate())
                itemWarp = ItemUtil.getBuilder(Material.SIGN)
                        .setName("§c" + warp.getName())
                        .setLore(lang.getList("MYWARP_ITEM_LORE_PRIVATE"))
                        .glowing()
                        .build();
            else
                itemWarp = ItemUtil.getBuilder(Material.SIGN)
                        .setName("§a" + warp.getName())
                        .setLore(lang.getList("MYWARP_ITEM_LORE_PUBLIC"))
                        .build();

            dInventory.setItem(pageNum, slot, new DItem(itemWarp, (player, clickType, i) -> {
                warp.setPrivate(!warp.isPrivate());
                SOUND_API.play(player, SoundType.SELECTED);
                update();
            }));

            if (slot == 16) {
                slot = 19;
            } else if (slot == 25) {
                slot = 28;
            } else if (slot == 34) {
                slot = 10;
                ++pageNum;
            } else {
                ++slot;
            }
        }

        setAction(dInventory, lang, size);
    }
}
