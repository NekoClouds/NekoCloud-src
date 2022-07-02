package me.nekocloud.skyblock.api;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.action.ClickAction;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.skyblock.api.manager.IslandManager;
import org.bukkit.entity.Player;

public abstract class SkyBlockGui {

    protected static final GuiManager<SkyBlockGui> SKY_GUI_MANAGER = SkyBlockAPI.getSkyGuiManager();
    protected static final InventoryAPI API = NekoCloud.getInventoryAPI();
    protected static final IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();
    protected static final String GUI_NAME_SKY_BLOCK = "ISLAND_PROFILE_GUI_NAME";
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    protected Player player;
    protected DInventory inventory;
    protected Language lang;

    protected boolean opened = false;

    protected SkyBlockGui(Player player) {
        create(player);
        inventory = API.createInventory(player, lang.getMessage(GUI_NAME_SKY_BLOCK), 5);

        setItems(player);
    }

    protected SkyBlockGui(Player player, String key) {
        create(player);
        inventory = API.createInventory(player, this.lang.getMessage(GUI_NAME_SKY_BLOCK)
                + " ▸ "
                + this.lang.getMessage(key), 5);

        setItems(player);
    }

    private void create(Player player) {
        this.lang = Language.RUSSIAN;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer != null)
            this.lang = gamer.getLanguage();

        this.player = player;
    }

    public void update() {
        if (inventory == null || player == null || !player.isOnline())
            return;

        if (opened)
            return;

        setItems(player);
    }

    protected abstract void setItems(Player player);

    public void open() {
        inventory.openInventory(player);
    }

    protected void setBack(Language lang, DInventory dInventory, ClickAction clickAction) {
        dInventory.setItem(4 * 9 + 4, new DItem(CustomItems.getBack(lang), clickAction));
    }
}
