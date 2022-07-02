package me.nekocloud.survival.commons.api;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.inventory.type.BaseInventory;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.MainGui;
import org.bukkit.entity.Player;

public abstract class CommonsSurvivalGui<T extends BaseInventory> {
    protected static final GuiManager<CommonsSurvivalGui> MANAGER = CommonsSurvivalAPI.getGuiManager();
    protected static final UserManager USER_MANAGER = CommonsSurvivalAPI.getUserManager();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    protected static ConfigData configData;

    protected Player player;
    protected T dInventory;
    protected Language lang;

    protected boolean opened = false;

    protected CommonsSurvivalGui(Player player) {
        this.player = player;
        this.createInventory();
        this.update();
    }

    protected abstract void createInventory();

    public void update() {
        if (dInventory == null)
            return;
        if (player == null || !player.isOnline())
            return;
        updateItems();
    }

    protected abstract void updateItems();

    public Player getPlayer() {
        return player;
    }

    public void open() {
        if (dInventory == null)
            return;
        if (player == null || !player.isOnline())
            return;
        dInventory.openInventory(player);
    }

    protected void setAction(MultiInventory multiInventory, Language lang, int size) {
        int pagesCount = InventoryUtil.getPagesCount(size, 21);
        if (size == 0)
            pagesCount = 1;
        for (DInventory inventory : multiInventory.getInventories()) {
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
            inventory.setItem(40, new DItem(CustomItems.getBack(lang), (player, clickType, slot) -> {
                SOUND_API.play(player, SoundType.PICKUP);
                MainGui mainGui = MANAGER.getGui(MainGui.class, player);
                if (mainGui != null)
                    mainGui.open();
            }));
        }
        INVENTORY_API.pageButton(lang, pagesCount, multiInventory, 38, 42);
    }

    @Override
    public String toString() {
        return "CommonsSurvivalGui{name = " + this.getClass().getSimpleName() + ", Player = {" + player.getName() + "}}";
    }

    public static void setConfigData(ConfigData configData) {
        CommonsSurvivalGui.configData = configData;
    }
}
