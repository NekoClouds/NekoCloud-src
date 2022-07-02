package me.nekocloud.packetlib.nms.interfaces;

import org.bukkit.Location;
import org.bukkit.World;

public interface DWorldBorder {

    World getWorld();
    void setWorld(World world);

    void setRadius(double size);

    double getRadius();

    void setCenter(Location center);
    void setCenter(double x, double z);

    void transitionSizeBetween(double size, double Oldradius, long delay);

    int getPortalTeleportBoundary();

    int getWarningTime();

    int getWarningDistance();

    void setWarningTime(int time);

    void setWarningDistance(int distance);

    long getSpeed();

    double getCenterX();
    double getCenterZ();

    double getOldRadius();
}
