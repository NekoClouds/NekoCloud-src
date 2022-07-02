package me.nekocloud.packetlib.libraries.inventory.inventories;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.packetlib.libraries.inventory.GuiManagerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class CraftDInventory implements DInventory {

    GuiManagerListener guiManagerListener;

    Inventory inventory;
    String name;
    int rows;

    Int2ObjectMap<DItem> items = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    @Setter @NonFinal
    private boolean disableAction;

    @Getter @NonFinal
    private InventoryAction inventoryAction = new InventoryAction() {
        @Override
        public void onOpen(final Player player) {
            //nothing((
        }

        @Override
        public void onClose(final Player player) {
            //nothing((
        }
    };


    public CraftDInventory(
            final Player player, final String name, final int rows,
            final InventoryAction inventoryAction, final GuiManagerListener guiManagerListener
    ) {
        this.name = name;
        this.rows = rows;
        this.guiManagerListener = guiManagerListener;

        inventory = Bukkit.createInventory(player, 9 * rows, name);

        this.disableAction = true;

        if (inventoryAction != null) {
            this.inventoryAction = inventoryAction;
        }
    }

    @Override
    public void openInventory(final Player player) {
        guiManagerListener.openInventory(player, this);
    }

    @Override
    public void openInventory(final BukkitGamer gamer) {
        val player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        openInventory(player);
    }

    @Override
    public void setItem(final int slot, final DItem item) {
        inventory.setItem(slot, item.getItem());
        items.put(slot, item);
    }

    @Override
    public void addItem(final DItem dItem) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                setItem(slot, dItem);
                return;
            }
        }
    }
    @Override
    public void removeItem(int slot) {
        val item = items.remove(slot);
        if (item == null) {
            return;
        }

        inventory.setItem(slot, null);
    }

    @Override
    public void clearInventory() {
        items.clear();
        inventory.clear();
    }

    @Override
    public int size() {
        return rows * 9;
    }

    @Override
    public void createInventoryAction(InventoryAction inventoryAction) {
        this.inventoryAction = inventoryAction;
    }
}
