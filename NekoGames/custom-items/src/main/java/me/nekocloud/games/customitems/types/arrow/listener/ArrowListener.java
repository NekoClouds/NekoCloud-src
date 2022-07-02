package me.nekocloud.games.customitems.types.arrow.listener;

import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.games.customitems.CustomItems;
import me.nekocloud.games.customitems.api.CustomItemType;
import me.nekocloud.games.customitems.api.CustomItemsAPI;
import me.nekocloud.games.customitems.api.arrow.CustomArrowEffect;
import me.nekocloud.games.customitems.manager.CustomItemsManager;
import me.nekocloud.games.customitems.types.arrow.CraftCustomArrow;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArrowListener extends DListener<CustomItems> {

    private final CustomItemsManager manager = CustomItemsAPI.getItemsManager();
    private final CustomItems plugin;
    private final Map<String, CraftCustomArrow> arrowsUsed = new HashMap<>();
    private final Map<Arrow, Entity> homingData = new ConcurrentHashMap<>();

    public ArrowListener(CustomItems plugin) {
        super(plugin);
        this.plugin = plugin;
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            for (Map.Entry<Arrow, Entity> entry : homingData.entrySet()) {
                Arrow arrow = entry.getKey();
                Entity target = entry.getValue();
                Vector newVelocity;
                double speed = arrow.getVelocity().length();
                if (arrow.isOnGround() || arrow.isDead() || target.isDead()) {
                    this.homingData.remove(arrow);
                    continue;
                }
                Vector toTarget = target.getLocation().clone().add(new Vector(0.0D, 0.5D, 0.0D))
                        .subtract(arrow.getLocation()).toVector();
                Vector dirVelocity = arrow.getVelocity().clone().normalize();
                Vector dirToTarget = toTarget.clone().normalize();
                double angle = dirVelocity.angle(dirToTarget);
                double newSpeed = 0.9D * speed + 0.14D;
                double distance = LocationUtil.distance(arrow.getLocation(), target.getLocation());
                if (target.getType() == EntityType.PLAYER && distance != -1 && distance < 64.0D) {
                    Player player = (Player) target;
                    if (player.isBlocking())
                        newSpeed = speed * 0.6D;
                }
                if (angle < 0.12D) {
                    newVelocity = dirVelocity.clone().multiply(newSpeed);
                } else {
                    Vector newDir = dirVelocity.clone().multiply((angle - 0.12D) / angle)
                            .add(dirToTarget.clone().multiply(0.12D / angle));
                    newDir.normalize();
                    newVelocity = newDir.clone().multiply(newSpeed);
                }
                arrow.setVelocity(newVelocity.add(new Vector(0.0D, 0.03D, 0.0D)));
            }
        }, 5, 1);
    }

    private boolean isShotFromMainHand(Player player) {
        ItemStack mainHand = player.getInventory().getItemInHand();
        return ((mainHand != null) && (mainHand.getType() == Material.BOW));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() != EntityType.TIPPED_ARROW)
            return;

        Arrow arrow = (Arrow) event.getEntity();
        ProjectileSource shooter = arrow.getShooter();
        if (!(shooter instanceof Player))
            return;

        Player player = (Player) shooter;
        PlayerInventory inventory = player.getInventory();

        ItemStack arrowItem = null;
        int slot = -1;
        for (ItemStack rotate : inventory.getContents()) {
            slot++;
            if (rotate == null || rotate.getType() != Material.TIPPED_ARROW)
                continue;

            arrowItem = rotate;
        }

        if (arrowItem == null)
            return;

        CraftCustomArrow customArrow = (CraftCustomArrow) this.manager.getItem(CustomItemType.ARROW, arrowItem);
        if (customArrow != null) {
            arrow.setMetadata("custom_arrow", new FixedMetadataValue(plugin, customArrow));
            if (!customArrow.hasEffect(CustomArrowEffect.HOMING))
                return;

            System.out.println("HOMING");
            double minAngle = Math.PI * 2;
            Entity minEntity = null;
            for (Entity entity : player.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
                if (player.hasLineOfSight(entity) && entity instanceof LivingEntity &&
                        !entity.isDead()) {
                    Vector toTarget = entity.getLocation().toVector().clone()
                            .subtract(player.getLocation().toVector());
                    double angle = arrow.getVelocity().angle(toTarget);
                    if (angle < minAngle) {
                        minAngle = angle;
                        minEntity = entity;
                    }
                }
                if (minEntity != null)
                    this.homingData.put(arrow, minEntity);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null ||
                (event.getEntityType() != EntityType.TIPPED_ARROW && event.getEntityType() != EntityType.TIPPED_ARROW))
            return;

        Arrow arrow = (Arrow) event.getEntity();
        if (!arrow.hasMetadata("custom_arrow"))
            return;

        CraftCustomArrow customArrow = (CraftCustomArrow) arrow.getMetadata("custom_arrow").get(0).value();
    }
}
