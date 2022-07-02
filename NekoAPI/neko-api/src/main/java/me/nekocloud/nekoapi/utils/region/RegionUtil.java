package me.nekocloud.nekoapi.utils.region;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegionUtil {

    public boolean isIn(String world1, String world2,
                        int x1, int y1, int z1, int x2, int y2, int z2, int x, int y, int z) {
        return world1.equals(world2) &&
                Math.min(x1, x2) <= x &&
                Math.min(y1, y2) <= y &&
                Math.min(z1, z2) <= z &&
                Math.max(x1, x2) >= x &&
                Math.max(y1, y2) >= y &&
                Math.max(z1, z2) >= z;
    }
}
