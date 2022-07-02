package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.api.entity.EquipType;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class DEntityBase<T extends net.minecraft.server.v1_12_R1.Entity> implements DEntity {

    protected T entity;

    protected DEntityBase(T entity) {
        this.entity = entity;
    }

    @Override
    public void setLocation(Location location) {
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public Location getLocation() {
        CraftWorld craftWorld = entity.world.getWorld();
        return new Location(craftWorld, entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
    }

    @Override
    public int getEntityID() {
        return entity.getId();
    }

    @Override
    public void setNoGravity(boolean gravity) {
        entity.setNoGravity(gravity);
    }

    @Override
    public boolean hasPassenger() {
        return entity.passengers != null && entity.passengers.size() > 0;
    }

    @Override
    public void setPassenger(DEntity dEntity) {
        removePassenger();
        net.minecraft.server.v1_12_R1.Entity entity = ((DEntityBase) dEntity).getEntityNms();
        this.entity.passengers.add(entity);
    }

    public T getEntityNms() {
        return entity;
    }

    @Override
    public String toString() {
        return entity.toString();
    }

    @Override
    public void removePassenger() {
        if (!hasPassenger())
            return;

        entity.passengers.clear();
    }

    @Override
    public boolean getCustomNameVisible() {
        return entity.getCustomNameVisible();
    }

    @Override
    public void setCustomName(String name) {
        entity.setCustomName(name);
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        entity.setCustomNameVisible(visible);
    }

    @Override
    public boolean isInvisible() {
        return entity.isInvisible();
    }

    @Override
    public boolean isOnGround() {
        return entity.onGround;
    }

    @Override
    public void watch(int type, byte value) {
        entity.getDataWatcher().set(new DataWatcherObject<>(type, DataWatcherRegistry.a), value);
    }

    @Override
    public UUID getUniqueID() {
        return entity.getUniqueID();
    }

    @Override
    public void setEquipment(EquipType equipment, ItemStack itemStack) {
        entity.setEquipment(EnumItemSlot.valueOf(equipment.name()), CraftItemStack.asNMSCopy(itemStack));
    }

    @Override
    public void setGlowing(boolean glowing) {
        entity.glowing = glowing;
        if (entity.getFlag(6) == glowing)
            return;

        entity.setFlag(6, glowing);
    }

    @Override
    public EntityType getType() {
        return getBukkitEntity().getType();
    }

    @Override
    public int getEntityTypeID() {
        return EntityTypes.b.a(entity.getClass());
    }

    @Override
    public Entity getBukkitEntity() {
        return entity.getBukkitEntity();
    }
}
