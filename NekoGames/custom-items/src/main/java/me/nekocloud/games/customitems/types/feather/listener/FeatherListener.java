package me.nekocloud.games.customitems.types.feather.listener;

import me.nekocloud.games.customitems.CustomItems;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import me.nekocloud.games.customitems.api.CustomItemsAPI;
import me.nekocloud.games.customitems.manager.CustomItemsManager;
import me.nekocloud.games.customitems.types.feather.CraftFeather;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class FeatherListener extends DListener<CustomItems> {

    private final CustomItemsManager manager = CustomItemsAPI.getItemsManager();
    private final Set<String> noFallPlayers = new HashSet<>();

    public FeatherListener(CustomItems plugin) {
        super(plugin);
    }

    private CustomItem getFeatherType(ItemStack item) {
        CustomItem feather = manager.getItem(CustomItemType.FEATHER, item);
        if (feather == null)
            feather = manager.getItem(CustomItemType.FEATHER_UP, item);

        return feather;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        CustomItem feather = getFeatherType(event.getItem());
        if (feather == null)
            return;

        Player player = event.getPlayer();
        if (feather.getType() == CustomItemType.FEATHER) {
            player.setVelocity(player.getVelocity().setY(((CraftFeather) feather).getMultipleY()));
            this.noFallPlayers.add(player.getName());
        } else {
            player.teleport(player.getWorld().getHighestBlockAt(player.getLocation()).getLocation());
        }
        ItemStack hand = player.getItemInHand();
        if (hand.getAmount() > 1)
            hand.setAmount(hand.getAmount() - 1);
        else
            player.setItemInHand(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER ||
                event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        Player player = (Player) event.getEntity();
        if (this.noFallPlayers.remove(player.getName()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.noFallPlayers.remove(event.getPlayer().getName());
    }
}
