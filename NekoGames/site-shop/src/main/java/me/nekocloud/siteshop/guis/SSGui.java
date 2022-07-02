package me.nekocloud.siteshop.guis;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.siteshop.ItemsLoader;
import me.nekocloud.siteshop.item.PlayerSSItem;
import me.nekocloud.siteshop.item.SSItem;
import me.nekocloud.siteshop.item.SSItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class SSGui {

    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    private final SSItemManager itemManager;
    private final BukkitGamer gamer;
    private final DInventory inventory;

    private final List<PlayerSSItem> items = new ArrayList<>();

    public SSGui(SSItemManager itemManager, BukkitGamer gamer) {
        this.itemManager = itemManager;
        this.gamer = gamer;

        this.inventory = INVENTORY_API.createInventory(
                gamer.getPlayer(),
                gamer.getLanguage().getMessage("SITE_SHOP_ITEMS_GUI_NAME"), 5);
    }

    public void loadFromMysql() {
        items.addAll(itemManager.getItemsLoader().loadItem(gamer, itemManager));
    }

    public void updateGui() {
        Language lang = gamer.getLanguage();

        if (items.isEmpty()) {
            inventory.setItem(2 * 9 + 4, new DItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                    .setName(lang.getMessage("SITE_SHOP_ITEM_EMPTY_NAME"))
                    .setLore(lang.getList("SITE_SHOP_ITEM_EMPTY_LORE"))
                    .removeFlags()
                    .build(), (player, clickType, i) -> SOUND_API.play(player, SoundType.NO)));
            return;
        }

        int slot = 10;
        ItemsLoader itemsLoader = itemManager.getItemsLoader();
        for (PlayerSSItem playerSSItem : items) {
            SSItem ssItem = playerSSItem.getSsItem();

            int finalSlot = slot;
            inventory.setItem(finalSlot, new DItem(ssItem.getIcon(lang, true),
                    (player, clickType, i) -> {
                if (!findSlots(player, ssItem)) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("SITE_SHOP_NO_SLOTS");
                    return;
                }
                if (!playerSSItem.isAllowed()) {
                    return;
                }
                playerSSItem.giveToPlayer(gamer, itemsLoader);
                SOUND_API.play(player, SoundType.DESTROY);
                inventory.removeItem(finalSlot);
            }));

            slot++;

            if ((slot - 1) % 8 == 0)
                slot += 2;
        }
    }

    public static boolean findSlots(Player player, SSItem ssItem) {
        int size = ssItem.getPurchasedItems().size();
        for (ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                size--;
            }
            if (size <= 0) {
                return true;
            }
        }
        return false;
    }

    public void open() {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        inventory.openInventory(player);
    }
}
