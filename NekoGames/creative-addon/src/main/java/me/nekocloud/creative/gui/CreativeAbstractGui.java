package me.nekocloud.creative.gui;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.Player;

public abstract class CreativeAbstractGui {

    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();

    protected final DInventory inventory;
    protected final Language lang;

    protected CreativeAbstractGui(String name, int rows, Language lang) {
        inventory = INVENTORY_API.createInventory(name, rows);
        this.lang = lang;
        this.setItems();
    }

    protected abstract void setItems();

    public final void open(Player player) {
        inventory.openInventory(player);
    }
}
