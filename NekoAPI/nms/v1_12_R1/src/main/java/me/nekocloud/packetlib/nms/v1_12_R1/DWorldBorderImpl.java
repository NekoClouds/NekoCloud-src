package me.nekocloud.packetlib.nms.v1_12_R1;

import me.nekocloud.packetlib.nms.interfaces.DWorldBorder;
import net.minecraft.server.v1_12_R1.WorldBorder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class DWorldBorderImpl extends WorldBorder implements DWorldBorder {

    private World world;

    DWorldBorderImpl(World world) {
        setWorld(world);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
        super.world = ((CraftWorld)world).getHandle();
    }

    @Override
    public void setRadius(double size) {
        super.setSize(size);
    }

    @Override
    public double getRadius() {
        return super.getSize();
    }

    @Override
    public void setCenter(Location center) {
        setCenter(center.getX(), center.getZ());
    }

    @Override
    public int getPortalTeleportBoundary() {
        return super.l();
    }

    @Override
    public long getSpeed() {
        return super.i();
    }

    @Override
    public double getOldRadius() {
        return super.j();
    }

    public WorldBorder get() {
        return this;
    }
}
