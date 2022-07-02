package me.nekocloud.packetlib.libraries.usableItem;

import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.usableitem.ClickType;
import me.nekocloud.api.usableitem.UsableItem;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsablesManager extends DListener<NekoAPI> {

    private final SoundAPI soundAPI = NekoCloud.getSoundAPI();
    @Getter
    private final Map<String, CraftUsableItem> usableItems = new ConcurrentHashMap<>();

    UsablesManager(NekoAPI nekoAPI) {
        super(nekoAPI);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack itemHand = e.getItem();
        Player player = e.getPlayer();
        if (itemHand == null) {
            return;
        }

        ItemMeta itemMeta = itemHand.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        String displayName = itemMeta.getDisplayName();
        if (displayName == null) {
            return;
        }

        CraftUsableItem usableItem = usableItems.get(displayName);
        if (usableItem == null) {
            return;
        }

        e.setCancelled(true);

        Action action = e.getAction();
        boolean left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        if (Cooldown.hasCooldown(player.getName(), "usables")) {
            return;
        }

        Cooldown.addCooldown(player.getName(), "usables", 20L);
        ClickType clickType = left ? ClickType.LEFT : ClickType.RIGHT;
        usableItem.getClickAction().onClick(player, clickType, e.getClickedBlock());
    }

    @EventHandler
    public void onPlayerClickOwnInv(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null
                || !e.getClickedInventory().equals(player.getInventory())
                || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null)
            return;

        ItemMeta itemMeta = clicked.getItemMeta();
        if (itemMeta == null)
            return;

        String displayName = itemMeta.getDisplayName();
        if (displayName == null) {
            return;
        }

        CraftUsableItem usableItem = usableItems.get(displayName);
        if (usableItem != null && usableItem.isInvClick()) {
            e.setCancelled(true);
            if (e.getClick() != org.bukkit.event.inventory.ClickType.CREATIVE) {
                soundAPI.play(player, SoundType.NOTE_BASS);
                usableItem.getClickAction().onClick(player, ClickType.RIGHT, null);
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        disable(e.getOffHandItem(), e);
        disable(e.getMainHandItem(), e);
    }

    private void disable(ItemStack itemStack, Cancellable e) {
        if (itemStack.getType() != Material.AIR && itemStack.getItemMeta() != null) {
            String displayName = itemStack.getItemMeta().getDisplayName();
            if (displayName == null) {
                return;
            }

            if (usableItems.get(displayName) != null) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack dropItem = e.getItemDrop().getItemStack();
        if (!dropItem.hasItemMeta()) {
            return;
        }

        String displayName = dropItem.getItemMeta().getDisplayName();
        if (displayName == null) {
            return;
        }

        UsableItem usableItem = usableItems.get(displayName);
        if (usableItem != null && !usableItem.isDrop()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuitPlayer(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();
        for (UsableItem usableItem : usableItems.values()) {
            Player owner = usableItem.getOwner();
            if (owner != null && owner.getName().equalsIgnoreCase(name)) {
                usableItem.remove();
            }
        }
    }
}
