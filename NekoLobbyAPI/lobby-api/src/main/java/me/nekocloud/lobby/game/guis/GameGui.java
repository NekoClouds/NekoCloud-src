package me.nekocloud.lobby.game.guis;

import me.nekocloud.api.CoreAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.base.locale.Language;

public abstract class GameGui {

    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    protected static final CoreAPI CORE_API = NekoCloud.getCoreAPI();

    protected final MultiInventory multiInventory;
    protected final Language lang;

    protected GameGui(String key, Language lang, Object... replaced) {
        this.multiInventory = INVENTORY_API.createMultiInventory(lang.getMessage(key, replaced), 5);
        this.lang = lang;
    }

    public final void update() {
        if (multiInventory == null) {
            return;
        }

        setItems();
    }

    protected abstract void setItems();

    public final void open(BukkitGamer gamer) {
        if (multiInventory == null) {
            return;
        }

        multiInventory.openInventory(gamer);
    }
}
