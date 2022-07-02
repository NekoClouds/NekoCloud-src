package me.nekocloud.market.auction.gui;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.market.auction.AuctionItemType;
import me.nekocloud.market.auction.AuctionManager;
import org.bukkit.entity.Player;

public class AuctionTypeMainGui {

    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private final AuctionManager manager;
    private final DInventory inventory;
    private final Language lang;

    public AuctionTypeMainGui(AuctionManager auctionManager, Language lang) {
        this.manager = auctionManager;
        this.lang = lang;
        this.inventory = INVENTORY_API.createInventory(lang.getMessage("AUCTION_MAINGUI_NAME"), 6);

        setItems();
    }

    private void setItems() {
        int slot = 10;
        for (AuctionItemType type : AuctionItemType.values()) {
            if (type == AuctionItemType.ALL)
                continue;
            inventory.setItem(slot++, new DItem(ItemUtil.getBuilder(type.getItem())
                    .removeFlags()
                    .setName("§a" + type.getName(lang))
                    .setLore(lang.getList("AUCTION_SUB_TYPE_LORE"))
                    .build(), (player, clickType, i) -> {
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null)
                    return;
                manager.openTypeGui(gamer, type);
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

        }

        inventory.setItem(49, new DItem(CustomItems.getBack2(lang), (player, clickType, i) -> {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                return;
            manager.openMainGui(gamer);
        }));
    }

    public void open(Player player) {
        if (inventory == null)
            return;
        inventory.openInventory(player);
    }
}
