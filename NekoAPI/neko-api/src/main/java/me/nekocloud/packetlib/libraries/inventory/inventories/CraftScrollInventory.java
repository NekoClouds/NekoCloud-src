package me.nekocloud.packetlib.libraries.inventory.inventories;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.ScrollInventory;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftScrollInventory implements ScrollInventory {
    private static final InventoryAPI API = NekoCloud.getInventoryAPI();

    private int position;
    private final DInventory inventory;
    private final int lang;
    private final List<DItem> scrollItem = new ArrayList<>();
    private final Map<Integer, DItem> defaultItems = new HashMap<>();

    @Getter
    @Setter
    private boolean disableAction;

    public CraftScrollInventory(Player player, String name, int lang) {
        inventory = API.createInventory(player, name, 5);
        this.lang = lang;
        this.position = 0;

        this.disableAction = true;
    }

    @Override
    public void openInventory(Player player) {
        inventory.openInventory(player);
    }

    @Override
    public void addItemScroll(DItem item) {
        scrollItem.add(item);
        setButtonScroll();
    }

    @Override
    public void addItemsScroll(List<DItem> items) {
        scrollItem.addAll(items);
        setButtonScroll();
    }

    @Override
    public void removeItemScroll(int numberItem) {
        scrollItem.remove(numberItem);
    }

    @Override
    public void removeItemsScroll() {
        scrollItem.clear();
    }

    @Override
    public void clearInventory() {
        inventory.clearInventory();
        scrollItem.clear();
        defaultItems.clear();
    }

    @Override
    public void openInventory(BukkitGamer gamer) {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline())
            return;

        openInventory(player);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public void setItem(int slot, DItem item) {
        inventory.setItem(slot, item);
        defaultItems.put(slot, item);
    }


    private void setButtonScroll() {
        //DItem up = API.createItem()
        /*
        if (i == 0 && pagesCount > 1) {
                (pages.getMessage(i)).setItem(slotUp, inventoryAPI.createItem(ItemUtil.createItemStack(
                        Material.ARROW,
                        Localization.getMessage(lang, "PAGE_ARROW1"),
                        Localization.getList(lang, "PAGE_ARROW_LORE", (i + 2))),
                        (achievement, clickType, slot) -> pages.getMessage(finalI + 1).openInventory(achievement)));
            } else if (i > 0 && i < pagesCount - 1) {
                (pages.getMessage(i)).setItem(slotDown, inventoryAPI.createItem(ItemUtil.createItemStack(
                        Material.ARROW,
                        Localization.getMessage(lang, "PAGE_ARROW2"),
                        Localization.getList(lang, "PAGE_ARROW_LORE", i)),
                        (achievement, clickType, slot) -> pages.getMessage(finalI - 1).openInventory(achievement)));
                (pages.getMessage(i)).setItem(slotUp, inventoryAPI.createItem(ItemUtil.createItemStack(
                        Material.ARROW,
                        Localization.getMessage(lang, "PAGE_ARROW1"),
                        Localization.getList(lang, "PAGE_ARROW_LORE", (i + 2))),
                        (achievement, clickType, slot) -> pages.getMessage(finalI + 1).openInventory(achievement)));
            } else if (pages.size() > 1 && pagesCount > 1) {
                (pages.getMessage(i)).setItem(slotDown, inventoryAPI.createItem(ItemUtil.createItemStack(
                        Material.ARROW,
                        Localization.getMessage(lang, "PAGE_ARROW2"),
                        Localization.getList(lang, "PAGE_ARROW_LORE", i)),
                        (achievement, clickType, slot) -> pages.getMessage(finalI - 1).openInventory(achievement)));
            }
         */
        //todo
    }
}
