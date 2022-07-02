package me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.AbstractPacket;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityDestroy(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getCount() {
        return handle.getIntegerArrays().read(0).length;
    }

    public int[] getEntityIDs() {
        return handle.getIntegerArrays().read(0);
    }
    public void setEntityIds(int[] value) {
        handle.getIntegerArrays().write(0, value);
    }

}
