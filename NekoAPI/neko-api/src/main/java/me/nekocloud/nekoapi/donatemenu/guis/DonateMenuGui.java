package me.nekocloud.nekoapi.donatemenu.guis;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.nekoapi.donatemenu.DonateMenuData;
import me.nekocloud.nekoapi.guis.CustomItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class DonateMenuGui {

    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();

    protected static final ItemStack NO_PERMS = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
            .setDurability((short) 14)
            .build();

    protected final Player player;
    protected final DonateMenuData donateMenuData;
    protected final MultiInventory inventory;

    DonateMenuGui(Player player, DonateMenuData donateMenuData, String name) {
        this.player = player;
        this.donateMenuData = donateMenuData;
        this.inventory = INVENTORY_API.createMultiInventory(player, name, 6);
    }

    protected final void setBack(DonateMenuGui gui) {
        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        inventory.setItem(49, new DItem(CustomItems.getBack2(gamer.getLanguage()),
                (clicker, clickType, slot) -> {
                    if (gui == null) {
                        return;
                    }
                    SOUND_API.play(player, SoundType.PICKUP);
                    gui.open();
        }));
    }

    protected abstract void setItems(BukkitGamer gamer);

    /**
     * Установить боковые стеклышки
     */
    protected final void setGlassItems() {
        for (int slot : CustomItems.EMPTY_SLOTS_PURPLE) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 10)
                    .build()));
        }
        for (int slot : CustomItems.EMPTY_SLOTS_MAGENTA) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 2)
                    .build()));
        }
    }

    public final void open() {
        if (inventory == null || player == null) {
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        setItems(gamer);
        inventory.openInventory(player);
    }
}
