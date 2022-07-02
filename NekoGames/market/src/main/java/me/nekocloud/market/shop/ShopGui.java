package me.nekocloud.market.shop;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.base.locale.Language;

import java.util.List;

@Getter
public class ShopGui {
    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();

    private final Language lang;
    private final String name;
    private final DInventory dInventory;
    private final List<ShopItem> items;

    public ShopGui(Language lang, String name, String nameGui, List<ShopItem> items) {
        this.lang = lang;
        this.name = name;
        this.dInventory = getDInventory();
        val dInventory = INVENTORY_API.createInventory(lang.getMessage(nameGui), 5); //5 строк
        this.items = items;

        this.items.forEach(shopItem -> dInventory.setItem(shopItem.getSlot(), shopItem.getDItem()));
    }
}
