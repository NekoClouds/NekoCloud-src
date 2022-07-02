package me.nekocloud.nekoapi.utils.region;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class Region  {

    @Getter
    String world;
    int x1;
    int y1;
    int z1;
    int x2;
    int y2;
    int z2;

    public Region(String world, Location min, Location max) {
        this(world, min.getBlockX(), min.getBlockY(), min.getBlockZ(),
                max.getBlockX(), max.getBlockY(), max.getBlockZ());
    }

    public boolean isInRegion(Location loc) {
        return RegionUtil.isIn(loc.getWorld().getName(), world, x1, y1, z1, x2, y2, z2,
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public boolean isInRegionWithAdditionRadius(Location loc, int radius) {
        return RegionUtil.isIn(loc.getWorld().getName(), world,
                x1 - radius, y1 - radius,
                z1 - radius, x2 + radius, y2 + radius, z2 + radius,

                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Block getAnyBlock() {
        World world = Bukkit.getWorld(this.world);

        for (int x = x1; x <= x2; ++x) {
            for (int y = y2; y >= y1; --y) {
                for (int z = z1; z <= z2; ++z) {
                    val loc = new Location(world, x, y, z);
                    if (loc.getBlock().getType() != Material.AIR) {
                        return loc.getBlock();
                    }
                }
            }
        }
        return null;
    }
}