package me.nekocloud.games.customitems.types.axe.listener;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.games.customitems.CustomItems;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemType;
import me.nekocloud.games.customitems.api.CustomItemsAPI;
import me.nekocloud.games.customitems.manager.CustomItemsManager;
import me.nekocloud.games.customitems.types.axe.CraftThrowingAxe;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ThrowingAxeListener extends DListener<CustomItems> {

    private final CustomItemsManager manager = CustomItemsAPI.getItemsManager();
    private final EntityAPI entityAPI = NekoCloud.getEntityAPI();
    private final CustomItems plugin;

    public ThrowingAxeListener(CustomItems plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThrow(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() != Action.RIGHT_CLICK_AIR || item == null)
            return;

        CustomItem customItem = this.manager.getItem(CustomItemType.THROW_AXE, item);
        if (customItem == null)
            return;

        Player player = event.getPlayer();
        if (item.getAmount() > 1)
            item.setAmount(item.getAmount() - 1);
        else
            player.setItemInHand(null);
        Vector vector = player.getLocation()
                .add(player.getLocation().getDirection().multiply(10))
                .toVector().subtract(player.getLocation().toVector())
                .normalize();
        throwAxe(player, vector, customItem);
    }

    public void throwAxe(Player player, Vector vector, CustomItem customItem) {
        Location to = new Location(player.getWorld(), player.getLocation().getX(),
                player.getLocation().getY() + 1.0, player.getLocation().getZ());
        CustomStand stand = this.entityAPI.createStand(to);
        stand.setInvisible(true);
        stand.setSmall(true);
        stand.setPublic(true);
        stand.getEntityEquip().setItemInMainHand(customItem.getItem().clone());
        rotateAxe(player, stand, vector.multiply(1.2), customItem);
    }

    public void rotateAxe(Player player, CustomStand stand, Vector vector, CustomItem customItem) {
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : stand.getLocation().getWorld().getNearbyEntities(stand.getLocation(), 1, 1, 1)) {
                    stand.remove();
                    ((Damageable) entity).damage(((CraftThrowingAxe) customItem).getDamage());
                    if (customItem.getItem().getEnchantments().containsKey(Enchantment.FIRE_ASPECT))
                        entity.setFireTicks(20);
                    return;
                }
                double x = stand.getRightArmPose().getX();
                stand.setRightArmPose(new EulerAngle(x + 0.6, 0.0, 0.0));
                Vector vec = new Vector(vector.getX(), vector.getY() - 0.03, vector.getZ());
                stand.onTeleport(stand.getLocation().clone().setDirection(vec));
            }
        }, 0, 2);
    }
}
