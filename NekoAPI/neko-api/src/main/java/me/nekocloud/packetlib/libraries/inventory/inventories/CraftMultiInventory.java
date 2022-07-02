package me.nekocloud.packetlib.libraries.inventory.inventories;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CraftMultiInventory implements MultiInventory {
    private static final InventoryAPI API = NekoCloud.getInventoryAPI();

    private final List<DInventory> inventories = new ArrayList<>();
    private final Player player;
    private final int rows;
    private final String name;

    public CraftMultiInventory(Player player, String name, int rows) {
        this.player = player;
        this.name = name;
        this.rows = rows;
        DInventory inventory = API.createInventory(player, name, rows);
        inventories.add(inventory);
    }

    @Override
    public void openInventory(Player player, int page) {
        DInventory inventory = inventories.get(page);
        if (inventory == null)
            inventory = inventories.get(0);

        inventory.openInventory(player);
    }

    @Override
    public void openInventory(Player player) {
        DInventory inventory = inventories.get(0);
        inventory.openInventory(player);
    }

    @Override
    public void openInventory(BukkitGamer gamer) {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline())
            return;

        openInventory(player);
    }

    @Override
    public void setDisableAction(boolean action) {
        inventories.forEach(dInventory -> dInventory.setDisableAction(action));
    }

    @Override
    public boolean isDisableAction() {
        return inventories.get(0).isDisableAction();
    }

    @Override
    public void setItem(int page, int slot, DItem item) {
        createPages(page);

        DInventory inventory = inventories.get(page);
        if (inventory == null) {
            return;
        }
        inventory.setItem(slot, item);
    }

    @Override
    public void addItem(int page, DItem item) {
        createPages(page);

        DInventory inventory = inventories.get(page);
        inventory.addItem(item);
    }

    @Override
    public void setItem(int slot, DItem item) {
        inventories.forEach(inv -> inv.setItem(slot, item));
    }

    @Override
    public void removeItem(int page, int slot) {
        if (page > inventories.size())
            return;

        DInventory inventory = inventories.get(page);
        inventory.removeItem(slot);
    }

    @Override
    public void removePage(int page) {
        inventories.remove(page);
    }

    @Override
    public void clearInventories() {
        inventories.forEach(DInventory::clearInventory);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return rows * 9;
    }

    @Override
    public int pages() {
        return inventories.size();
    }

    @Override
    public List<DInventory> getInventories() {
        return inventories;
    }

    private void createPages(int page) {
        page += 1;
        if (page > inventories.size()) {
            for (int i = 0; i < page; i++) {
                if (inventories.size() >= (i + 1))
                    continue;

                if (i == 0)
                    continue;

                inventories.add(API.createInventory(player, name + " | " + (i + 1), rows));
            }
        }
    }
}
