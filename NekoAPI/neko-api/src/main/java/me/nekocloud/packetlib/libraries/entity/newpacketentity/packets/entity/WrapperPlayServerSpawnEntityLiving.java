package me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

//todo чекать локалицию и исправлять ее
public class WrapperPlayServerSpawnEntityLiving extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;

    private static PacketConstructor entityConstructor;

    public WrapperPlayServerSpawnEntityLiving() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityLiving(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerSpawnEntityLiving(Entity entity) {
        super(fromEntity(entity), TYPE);
    }

    // Useful constructor
    private static PacketContainer fromEntity(Entity entity) {
        if (entityConstructor == null)
            entityConstructor = ProtocolLibrary.getProtocolManager()
                            .createPacketConstructor(TYPE, entity);
        return entityConstructor.createPacket(entity);
    }

    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public UUID getUniqueId() {
        return handle.getUUIDs().read(0);
    }

    public void setUniqueId(UUID value) {
        handle.getUUIDs().write(0, value);
    }

    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    public EntityType getType() {
        return EntityType.fromId(handle.getIntegers().read(1));
    }

    public void setType(EntityType value) {
        handle.getIntegers().write(1, (int) value.getTypeId());
    }

    public double getX() {
        return handle.getDoubles().read(0);
    }

    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    public double getY() {
        return handle.getDoubles().read(1);
    }

    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    public double getZ() {
        return handle.getDoubles().read(2);
    }

    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadPitch() {
        return (handle.getBytes().read(2) * 360.F) / 256.0F;
    }

    public void setHeadPitch(float value) {
        handle.getBytes().write(2, (byte) (value * 256.0F / 360.0F));
    }

    public double getVelocityX() {
        return handle.getIntegers().read(2) / 8000.0D;
    }

    public void setVelocityX(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }

    public double getVelocityY() {
        return handle.getIntegers().read(3) / 8000.0D;
    }

    public void setVelocityY(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }

    public double getVelocityZ() {
        return handle.getIntegers().read(4) / 8000.0D;
    }

    public void setVelocityZ(double value) {
        handle.getIntegers().write(4, (int) (value * 8000.0D));
    }

    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }

    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
}
