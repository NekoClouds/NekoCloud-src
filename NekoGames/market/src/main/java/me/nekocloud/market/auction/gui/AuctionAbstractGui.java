package me.nekocloud.market.auction.gui;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.market.auction.AuctionManager;
import org.bukkit.entity.Player;

public abstract class AuctionAbstractGui {

    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    protected final AuctionManager manager;
    protected final Language lang;
    protected final MultiInventory inventory;

    AuctionAbstractGui(AuctionManager manager, Language lang, String name) {
        this.manager = manager;
        this.lang = lang;
        inventory = INVENTORY_API.createMultiInventory(name, 6);
        update();
    }

    public void update() {
        if (inventory == null)
            return;

        setItems();
    }

    public void open(Player player) {
        if (inventory == null)
            return;

        inventory.openInventory(player);
    }

    protected abstract void setItems();
}
