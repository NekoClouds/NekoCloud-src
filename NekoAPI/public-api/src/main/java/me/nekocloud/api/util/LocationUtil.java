package me.nekocloud.api.util;

import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class LocationUtil {

    public Location getCenter(List<Location> locations) {
        double x = 0;
        double y = 0;
        double z = 0;
        World world = locations.stream().findFirst().get().getWorld();

        float yaw = 0.0f;
        float pitch = 0.0f;

        for (Location location : locations) {
            x += location.getX();
            y += location.getY();
            z += location.getZ();
            yaw = location.getYaw();
            pitch = location.getPitch();
        }

        int size = locations.size();

        return new Location(world, x / size, y / size, z / size, yaw, pitch);
    }

    public List<Location> getLoc(FileConfiguration config, String patch) {
        return config.getStringList(patch)
                .stream()
                .map(spawnLoc -> LocationUtil.stringToLocation(spawnLoc, true))
                .filter(loc -> loc.getWorld() != null)
                .collect(Collectors.toList());
    }

    public byte getFixRotation(float yawPitch) {
        return (byte) (yawPitch * 256.0F / 360.0F);
    }

    public Location faceEntity(Location location, Entity entity) {
        Vector direction = location.toVector().subtract(entity.getLocation().toVector());
        direction.multiply(-1);
        location.setDirection(direction);
        return location;
    }

    public void setYawPitchEntity(Entity entity, Location center) {
        Location entityLoc = entity.getLocation();
        Vector direction = entityLoc.toVector().subtract(center.toVector());
        direction.multiply(-1);
        entityLoc.setDirection(direction);
        entity.teleport(entityLoc);
    }

    public double distance(Location location1, Location location2){
        if (location1.getWorld().equals(location2.getWorld())){
            return location1.distance(location2);
        }
        return -1;
    }

    public Location stringToLocation(String loc, boolean pitchAndYaw) {
        String[] locSplit = loc.split(";");
        Location location = new Location(Bukkit.getWorld(locSplit[0]),
                Double.parseDouble(locSplit[1]),
                Double.parseDouble(locSplit[2]),
                Double.parseDouble(locSplit[3]));
        if (pitchAndYaw && locSplit.length == 6) {
            location.setPitch(Float.parseFloat(locSplit[4]));
            location.setYaw(Float.parseFloat(locSplit[5]));
        }
        return location;
    }

    public String locationToString(Location loc, boolean pitchAndYaw) {
        StringBuilder locString = new StringBuilder();
        locString.append(loc.getWorld().getName())
                .append(";")
                .append(loc.getX())
                .append(";")
                .append(loc.getY())
                .append(";")
                .append(loc.getZ());
        if (pitchAndYaw) {
            locString.append(";")
                    .append(loc.getPitch())
                    .append(";")
                    .append(loc.getYaw());
        }

        return locString.toString();
    }

    public void loadChunk(Location location) {
        Chunk chunk = location.getChunk();
        if (chunk.isLoaded()) {
            return;
        }

        chunk.load();
    }

    public Location getDirection(Location location, double amount) {
        int degrees = (Math.round(location.getYaw()) + 270) % 360;
        if (degrees <= 22) return location.subtract(amount, 0.0, 0.0);
        //if (degrees <= 67) return "Northeast";
        if (degrees <= 112) return location.subtract(0.0, 0.0, amount);
        //if (degrees <= 157) return "Southeast";
        if (degrees <= 202) return location.subtract(-amount, 0.0, 0.0);
        //if (degrees <= 247) return "Southwest";
        if (degrees <= 292) return location.subtract(0.0, 0.0, amount);
        //if (degrees <= 337) return "Northwest";
        return location.subtract(amount, 0.0, 0.0);
    }

    public boolean compareLocations(Location loc1, Location loc2) {
        return loc1.getWorld().getName().equals(loc2.getWorld().getName())
                && loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public Location getSecondBedLocation(Location bedLocation) {
        for(int x = -1; x <= 1; ++x) {
            for(int z = -1; z <= 1; ++z) {
                for(int y = -1; y <= 1; ++y) {
                    Block bedBlock = bedLocation.getBlock().getRelative(x, y, z);
                    if (!bedLocation.equals(bedBlock.getLocation()) && bedBlock.getType().equals(Material.BED_BLOCK)) {
                        return bedBlock.getLocation();
                    }
                }
            }
        }

        return null;
    }
}
