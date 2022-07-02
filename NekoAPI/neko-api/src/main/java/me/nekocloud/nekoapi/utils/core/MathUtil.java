package me.nekocloud.nekoapi.utils.core;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MathUtil {

    public List<Location> getCircleUp(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double y = center.getY() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, center.getX(), y, z));
        }
        return locations;
    }

    public List<Location> getCircleSide(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public Vector rotateAroundAxisY(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public double randomDouble(double min, double max) {
        return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
    }

    public Vector getBackVector(Location loc) {
        float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 90))));
        float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 90))));
        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    public Vector rotateAroundAxisZ(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public long pairInt(int x, int y) {
        return (long)x << 32 | (long)y & 4294967295L;
    }

    public List<Double> getCircleYaw(int amount) {
        double increment = (2 * Math.PI) / amount;
        ArrayList<Double> values = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            values.add(i * increment);
        }
        return values;
    }

    public Vector getVector(Location fromLoc, Location toLoc){
        Vector from = new Vector(fromLoc.getX(), fromLoc.getY(), fromLoc.getZ());
        Vector to  = new Vector(toLoc.getX(), toLoc.getY(), toLoc.getZ());
        return to.subtract(from);
    }

    public int floor(double value) {
        int floor = (int)value;
        return value < (double)floor ? floor - 1 : floor;
    }
}
