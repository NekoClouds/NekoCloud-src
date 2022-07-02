package me.nekocloud.skyblock.listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.events.UserChangeFlyStatusEvent;
import me.nekocloud.survival.commons.api.events.UserChangeGodModeEvent;
import me.nekocloud.survival.commons.api.events.UserEvent;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.object.CraftWarp;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import me.nekocloud.skyblock.SkyBlock;
import me.nekocloud.skyblock.utils.FaweUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PvpListener extends DListener<SkyBlock> {

    private final String warpPvPName = "pvp";

    private final NmsManager nmsManager = NmsAPI.getManager();
    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();

    private final World pvpWorld;
    private final CuboidRegion region;

    public PvpListener(SkyBlock javaPlugin, World pvpWorld) {
        super(javaPlugin);

        this.pvpWorld = pvpWorld;

        pvpWorld.setThundering(false);
        pvpWorld.setGameRuleValue("doTileDrops", "false");

        Warp warp = CommonsSurvivalAPI.getWarpManager().getWarp(warpPvPName);
        if (warp == null) {
            warp = new CraftWarp(warpPvPName, 1, new Location(pvpWorld, 0, 68, -1), false);
            CommonsSurvivalAPI.getWarpManager().addWarp(warp);
        }

        Location up = new Location(pvpWorld, -6, 65, 4);
        Location down = new Location(pvpWorld, 6, 80, -8);
        region = FaweUtils.getRegion(up, down);
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectile(ProjectileHitEvent e) { //запретить удочкой по мобу
        Projectile projectile = e.getEntity();
        Entity entity = e.getHitEntity();
        if (entity == null || !(projectile.getShooter() instanceof Player)) {
            return;
        }

        if (!isLobbyWorld(entity) && !isSaveZona(entity)) {
            return;
        }

        projectile.remove();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        if (!isLobbyWorld(entity) && !isSaveZona(entity)) {
            return;
        }

        Player player = (Player) e.getEntity();
        e.setCancelled(player.getFoodLevel() >= e.getFoodLevel());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        if (!isLobbyWorld(player) && !isSaveZona(player)) {
            return;
        }

        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
            nmsManager.disableFire(player);

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent e) { //при смене мира офаем то, что нужно офнуть
        Player player = e.getPlayer();

        if (pvpWorld != null && player.getLocation().getWorld().getName().equalsIgnoreCase(pvpWorld.getName())) {
            User user = userManager.getUser(player);
            if (user == null) {
                return;
            }

            user.setGod(false, true);
            user.setFly(false, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeGod(UserChangeGodModeEvent e) {
        onDisable(e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeFly(UserChangeFlyStatusEvent e) {
        onDisable(e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleportEvent(UserTeleportByCommandEvent e) {
        switch (e.getCommand()) {
            case TPPOS:
            case CHUNK:
            case TOP:
            case JUMP:
                onDisable(e);
                break;
        }

    }

    private void onDisable(UserEvent e) {
        User user = e.getUser();
        Player player = user.getPlayer();
        if (player == null) {
            return;
        }

        if (pvpWorld != null && player.getLocation().getWorld().getName().equalsIgnoreCase(pvpWorld.getName())) {
            e.setCancelled(true);
        }
    }

    private boolean isSaveZona(Entity entity) {
        Location location = entity.getLocation();
        Vector vector = new Vector(location.getX(), location.getY(), location.getZ());
        return region != null && region.contains(vector);
    }

    private boolean isLobbyWorld(Entity entity) {
        return entity.getWorld().getName().equalsIgnoreCase("lobby");
    }
}
