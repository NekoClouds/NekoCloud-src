package me.nekocloud.packetlib.libraries.inventory;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.action.ClickAction;
import me.nekocloud.api.inventory.action.ClickActionWithCursor;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GuiManagerListener extends DListener<NekoAPI> {

    private final Map<String, DInventory> inventories = new HashMap<>();
    private final Map<String, DInventory> old_inventories = new HashMap<>();

    GuiManagerListener(NekoAPI nekoAPI) {
        super(nekoAPI);
    }

    public void openInventory(Player player, DInventory inventory) {
        String name = player.getName();

        old_inventories.put(name, inventory);

        DInventory oldInventory = inventories.get(name);
        if (oldInventory != null) {
            oldInventory.getInventoryAction().onClose(player);
        }

        inventories.put(name, inventory);
        inventory.getInventoryAction().onOpen(player);
        player.openInventory(inventory.getInventory());
        old_inventories.remove(name);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent e) {
        DInventory dInventory = getInv(e);
        if (dInventory == null) {
            return;
        }

        Inventory inv = e.getView().getTopInventory();
        if (!inv.getName().equals(dInventory.getName())) {
            return;
        }

        e.setCancelled(e.getClick().isShiftClick() || dInventory.isDisableAction());

        int slot = e.getRawSlot();

        if (slot >= 0 && slot < inv.getSize()) {

            e.setCancelled(true);

            DItem dItem = dInventory.getItems().get(slot);
            if (dItem == null || inv.getItem(slot) == null) {
                return;
            }

            ClickAction clickAction = dItem.getClickAction();
            if (clickAction instanceof ClickActionWithCursor) {
                ((ClickActionWithCursor) clickAction).setCursor(e.getCursor());
            }

            clickAction.onClick((Player) e.getWhoClicked(), e.getClick(), slot);

            if (clickAction instanceof ClickActionWithCursor) {
                e.setCursor(((ClickActionWithCursor) clickAction).getCursor());
            }
        }
    }

    @EventHandler
    public void onDisableDrag(InventoryDragEvent e) {
        DInventory dInventory = getInv(e);
        if (dInventory == null) {
            return;
        }

        if (!e.getInventory().getName().equalsIgnoreCase(dInventory.getName())) {
            return;
        }

        e.setCancelled(dInventory.isDisableAction());
        for (int slot : e.getRawSlots()) {
            if (slot >= 0 && slot < dInventory.size()) {
                e.setCancelled(true);
            }
        }

    }

    @Nullable
    private DInventory getInv(InventoryInteractEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            e.setCancelled(true);
            return null;
        }

        return inventories.get(e.getWhoClicked().getName());
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        String name = player.getName();
        DInventory dInventory = inventories.get(name);
        if (dInventory == null || dInventory == old_inventories.get(name)) {
            return;
        }

        inventories.remove(name);
        dInventory.getInventoryAction().onClose(player);
    }
}
