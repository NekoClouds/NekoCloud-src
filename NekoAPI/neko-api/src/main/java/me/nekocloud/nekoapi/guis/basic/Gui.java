package me.nekocloud.nekoapi.guis.basic;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import org.bukkit.Material;

public abstract class Gui {
    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    protected final GuiDefaultContainer listener;
    protected final DInventory dInventory;
    protected final Language lang;

    protected Gui(GuiDefaultContainer listener, Language lang, String name) {
        this.listener = listener;
        this.lang = lang;
        dInventory = INVENTORY_API.createInventory(name, 6);
        this.setItems();
    }

    public DInventory getInventory() {
        return dInventory;
    }

    protected abstract void setItems();

    /**
     * Установить боковые стеклышки
     */
    protected final void setGlassItems() {
        for (int slot : CustomItems.EMPTY_SLOTS_PURPLE) {
            dInventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 10)
                    .build()));
        }
        for (int slot : CustomItems.EMPTY_SLOTS_MAGENTA) {
            dInventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 2)
                    .build()));
        }
    }

    protected final void setGlassItemsNew() {
        for (int slot : CustomItems.EMPTY_SLOTS_PURPLE) {
            dInventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.getMaterial("GLASS_PANE"))
                    .setName(" ")
                    .setDurability((short) 10)
                    .build()));
        }
        for (int slot : CustomItems.EMPTY_SLOTS_MAGENTA) {
            dInventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.getMaterial("GLASS_PANE"))
                    .setName(" ")
                    .setDurability((short) 2)
                    .build()));
        }
    }

    /**
     * Установить кнопку назад
     */
    protected final void setBackItem() {
        dInventory.setItem(49, new DItem(CustomItems.getBack2(lang),
                (player, clickType, slot) -> {
                    SOUND_API.play(player, SoundType.CLICK);
                    listener.openGui(DonateGui.class, player);
                }));
    }
}
