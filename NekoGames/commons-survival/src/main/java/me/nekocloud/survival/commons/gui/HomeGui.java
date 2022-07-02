package me.nekocloud.survival.commons.gui;

import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.api.Home;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HomeGui extends CommonsSurvivalGui<MultiInventory> {

    public HomeGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;
        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createMultiInventory(player, lang.getMessage("ALTERNATE_GUI_NAME")
                + " ▸ " + lang.getMessage("HOME_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        CraftUser craftUser = (CraftUser) USER_MANAGER.getUser(player);
        if (craftUser == null)
            return;

        int size = craftUser.getHomes().size();

        dInventory.clearInventories();

        if (size == 0) {
            dInventory.setItem(0,22, new DItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                            .setName(lang.getMessage("HOME_ITEM_EMPTY_NAME"))
                            .setLore(lang.getList("HOME_ITEM_EMPTY_LORE"))
                            .build(),
                    (player, clickType, slot1) -> SOUND_API.play(player, SoundType.TELEPORT)));
            setAction(dInventory, lang, size);
            return;
        }

        int pageNum = 0;
        int slot = 10;
        int count = 0;
        for (String home : craftUser.getHomes().keySet()) {
            if (dInventory.getInventories().size() == 0)
                continue;
            count++;

            Home h = craftUser.getHomes().get(home);
            if (h == null)
                continue;

            Location loc = h.getLocation();

            dInventory.setItem(pageNum, slot, new DItem(ItemUtil.getBuilder(Material.BANNER)
                    .setDurability((short) count)
                    .setName("§a" + home)
                    .setLore(lang.getList("HOME_LORE", loc.getWorld().getName(),
                            String.valueOf((int) loc.getX()), String.valueOf((int) loc.getY()),
                            String.valueOf((int) loc.getZ())))
                    .build(), (player, clickType, i) -> {
                player.chat("/homes " + home);
                SOUND_API.play(player, SoundType.SELECTED);
                player.closeInventory();
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
