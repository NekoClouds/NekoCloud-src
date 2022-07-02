package me.nekocloud.api.gui;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.inventory.type.BaseInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultGui<T extends BaseInventory> {

    protected static final GuiManager<DefaultGui<?>> MANAGER = NekoCloud.getGuiManager();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    protected Player player;
    protected BukkitGamer gamer;
    protected T inventory;
    protected Language lang;

    protected boolean opened = false;

    protected DefaultGui(final Player player) {
        this.player = player;
        this.gamer = GAMER_MANAGER.getGamer(player);

        this.createInventory();
        this.update();
    }

    protected abstract void createInventory();

    public void update() {
        if (inventory == null)
            return;
        if (player == null || !player.isOnline())
            return;
        updateItems();
    }

    protected abstract void updateItems();

    public final Player getPlayer() {
        return player;
    }
    public final BukkitGamer getGamer() {
        return gamer;
    }

    public void open() {
        if (inventory == null)
            return;
        if (player == null || !player.isOnline())
            return;
        inventory.openInventory(player);
    }

    protected void setAction(
            final @NotNull MultiInventory multiInventory,
            final @NotNull Language lang,
            final int size
    ) {
        int pagesCount = InventoryUtil.getPagesCount(size, 21);
        if (size == 0)
            pagesCount = 1;
        for (val inventory : multiInventory.getInventories()) {
            inventory.createInventoryAction(new InventoryAction() {
                @Override
                public void onOpen(Player player) {
                    opened = true;
                }
                @Override
                public void onClose(Player player) {
                    opened = false;
                }
            });
//            inventory.setItem(40, new DItem(CustomItems.getBack(lang), (player, clickType, slot) -> {
//                SOUND_API.play(player, SoundType.PICKUP);
//                val mainGui = MANAGER.getGui(MainGui.class, player);
//                if (mainGui != null)
//                    mainGui.open();
//            }));
        }
        INVENTORY_API.pageButton(lang, pagesCount, multiInventory, 38, 42);
    }

    @Override
    public String toString() {
        return "DefaultGui(name = " + this.getClass().getSimpleName() + ", owner = " + player.getName() + ")";
    }
}
