package me.nekocloud.siteshop.guis;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.siteshop.item.SSItem;
import me.nekocloud.siteshop.item.SSItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class BuySGui {

    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    private final MultiInventory inventory;
    private final Language language;

    public BuySGui(Language language) {
        this.inventory = INVENTORY_API.createMultiInventory(language.getMessage("SITE_SHOP_ITEMS_NAME"), 5);
        this.language = language;
    }

    public void setItems(SSItemManager ssItemManager) {
        int slot = 10;
        int page = 0;

        for (SSItem ssItem : ssItemManager.getItems().valueCollection()) {
            inventory.setItem(page, slot++, new DItem(ssItem.getIcon(language, false),
                    (player, clickType, i) -> {
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null) {
                    SOUND_API.play(player, SoundType.NO);
                    return;
                }

                if (!SSGui.findSlots(player, ssItem)) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("SITE_SHOP_NO_SLOTS");
                    return;
                }

                if (!gamer.changeMoney(PurchaseType.VIRTS, -ssItem.getPrice())) {
                    player.closeInventory();
                    return;
                }

                SOUND_API.play(player, SoundType.LEVEL_UP);
                gamer.sendMessageLocale("SITE_SHOP_ITEM_ALERT");

                PlayerInventory inventory = player.getInventory();
                ssItem.getPurchasedItems().forEach(inventory::addItem);
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        INVENTORY_API.pageButton(language, page + 1, inventory, 38, 42);
    }

    public void open(Player player) {
        if (inventory != null) {
            inventory.openInventory(player);
        }
    }
}
