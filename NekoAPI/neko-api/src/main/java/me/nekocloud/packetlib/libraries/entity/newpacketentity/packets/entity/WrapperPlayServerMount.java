package me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerMount extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.MOUNT;

    public WrapperPlayServerMount() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMount(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    public int[] getPassengerIds() {
        return handle.getIntegerArrays().read(0);
    }

    public void setPassengerIds(int[] value) {
        handle.getIntegerArrays().write(0, value);
    }
}
