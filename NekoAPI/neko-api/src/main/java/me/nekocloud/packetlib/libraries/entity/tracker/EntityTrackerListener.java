package me.nekocloud.packetlib.libraries.entity.tracker;

import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.packetreader.event.AsyncChunkSendEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EntityTrackerListener extends DListener<NekoAPI> {

    private final TrackerManager trackerManager;

    public EntityTrackerListener(NekoAPI nekoAPI, TrackerManager trackerManager) {
        super(nekoAPI);
        this.trackerManager = trackerManager;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (BukkitGamer gamer : GAMER_MANAGER.getGamers().values()) {
                String name = gamer.getName().toLowerCase();
                Player player = gamer.getPlayer();
                if (player == null || !player.isOnline()) {
                    continue;
                }

                for (TrackerEntity trackerEntity : trackerManager.getTrackerEntities()) {
                    if (!trackerEntity.isHeadLook()) {
                        continue;
                    }

                    Location location = trackerEntity.getLocation();
                    double distance = LocationUtil.distance(location, player.getLocation());
                    if (location.getWorld() == player.getWorld() && trackerEntity.canSee(player)) {
                        Set<String> names = trackerEntity.getHeadPlayers();
                        if (distance < 5 && distance != -1) {
                            names.add(name);
                            location = LocationUtil.faceEntity(trackerEntity.getLocation(), player).clone();
                            trackerEntity.sendHeadRotation(player, location.getYaw(), location.getPitch());
                        } else if (names.contains(name)) {
                            names.remove(name);
                            trackerEntity.sendHeadRotation(player, location.getYaw(), location.getPitch());
                        }

                    }
                }
            }
        }, 5, 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onJoin(AsyncChunkSendEvent e) {
        Player player = e.getPlayer();
        if (player == null)
            return;

        String worldName = e.getWorldName();
        int x = e.getX();
        int z = e.getZ();

        for (TrackerEntity trackerEntity : trackerManager.getTrackerEntities()) { //todo подобное есть в АПИ для ViaVersion
            if (!trackerEntity.getLocation().getWorld().getName().equalsIgnoreCase(worldName)) {
                continue;
            }

            int trackedEntityX = trackerEntity.getLocation().getBlockX() >> 4;
            int trackedEntityZ = trackerEntity.getLocation().getBlockZ() >> 4;
            if (trackedEntityX == x && trackedEntityZ == z && trackerEntity.canSee(player)) {
                trackerEntity.destroy(player);
                BukkitUtil.runTaskLaterAsync(10L, () -> trackerEntity.spawn(player));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(AsyncGamerQuitEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        for (TrackerEntity trackerEntity : trackerManager.getTrackerEntities()) {
            trackerEntity.removeTo(player);
            if (trackerEntity.getOwner() != null && player.getName().equals(trackerEntity.getOwner().getName()))
                trackerEntity.remove();
        }
    }

    /*
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();

        BukkitUtil.runTaskAsync(() -> {
            for (TrackerEntity trackerEntity : trackerManager.getTrackerEntities()) {
                if (trackerEntity.getLocation().getWorld() == e.getFrom() && trackerEntity.canSee(player)) {
                    trackerEntity.destroy(player);
                }
            }
        });
    }
    */
}
