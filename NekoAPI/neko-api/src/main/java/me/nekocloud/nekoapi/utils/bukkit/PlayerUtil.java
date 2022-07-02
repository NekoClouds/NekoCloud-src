package me.nekocloud.nekoapi.utils.bukkit;

import lombok.experimental.UtilityClass;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerUtil {

    private final NmsManager NMS_MANAGER = NmsAPI.getManager();

    public Collection<Player> getNearbyPlayers(Player player, int radius) {
        return player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Collection<Player> getNearbyPlayers(Location location, int radius){
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> LocationUtil.distance(player.getLocation(), location) <= radius
                        && LocationUtil.distance(player.getLocation(), location) != -1)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public boolean havePotionEffectType(Player player, PotionEffectType potionEffectType) {
        return player.getActivePotionEffects().stream()
                .filter(potionEffect -> potionEffect.getType() == potionEffectType).count() > 0;
    }

    public int getPotionEffectLevel(Player player, PotionEffectType potionEffectType) {
        PotionEffect effect = player.getActivePotionEffects().stream()
                .filter(potionEffect -> potionEffect.getType() == potionEffectType)
                .findFirst()
                .orElse(null);
        return effect != null ? effect.getAmplifier() : -1;
    }

    public void removePotionEffect(Player player, PotionEffectType potionEffectType) {
        player.removePotionEffect(potionEffectType);
    }

    public void addPotionEffect(Player player, PotionEffectType potionEffectType, int level){
        if (player == null) {
            return;
        }

        if (havePotionEffectType(player, potionEffectType))
            removePotionEffect(player, potionEffectType);

        player.addPotionEffect(new PotionEffect(potionEffectType, Integer.MAX_VALUE, level));
    }

    public void addPotionEffect(Player player, PotionEffectType potionEffectType, int level, int seconds){
        if (havePotionEffectType(player, potionEffectType))
            removePotionEffect(player, potionEffectType);

        player.addPotionEffect(new PotionEffect(potionEffectType, seconds*20, level));
    }

    public void reset(Player player) {
        if (player == null || !player.isOnline())
            return;

        try {
            player.getActivePotionEffects().forEach(potionEffect ->
                    player.removePotionEffect(potionEffect.getType()));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setExp(0.0f);
            player.setLevel(0);
            player.setFireTicks(0);

            player.closeInventory();
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        } catch (Exception ignored) {}

        BukkitUtil.runTask(()-> {
            if (!player.isOnline())
                return;
            NMS_MANAGER.disableFire(player);
            NMS_MANAGER.removeArrowFromPlayer(player);
        });
    }

    public Player getDamager(final Entity entity) {
        if (entity instanceof Player)
            return (Player) entity;

        Entity damager = null;
        if (entity instanceof Projectile projectile) {
            damager = (projectile.getShooter() instanceof Entity)
                    ? (Entity) projectile.getShooter() : null;
        }

        if (damager == null)
            return null;

        if (damager instanceof Player)
            return (Player) damager;

        return null;
    }
}
