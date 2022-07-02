package me.nekocloud.siteshop.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.siteshop.ItemsLoader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@RequiredArgsConstructor
@Getter
public final class PlayerSSItem {

    private final SSItem ssItem;

    private boolean allowed = true;

    public void giveToPlayer(BukkitGamer gamer, ItemsLoader itemsLoader) {
        allowed = false;

        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        gamer.sendMessageLocale("SITE_SHOP_ITEM_ALERT");
        itemsLoader.giveToPlayer(gamer, ssItem);

        PlayerInventory inventory = player.getInventory();
        ssItem.getPurchasedItems().forEach(inventory::addItem);
    }
}
