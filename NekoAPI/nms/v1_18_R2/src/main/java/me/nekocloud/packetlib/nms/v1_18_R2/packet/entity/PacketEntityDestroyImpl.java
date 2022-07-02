package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketEntityDestroy;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.DPacketBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;

public class PacketEntityDestroyImpl extends DPacketBase<PacketPlayOutEntityDestroy> implements PacketEntityDestroy {

    private int[] entityIDs;

    public PacketEntityDestroyImpl(int... entityIDs) {
        this.entityIDs = entityIDs;
    }

    @Override
    protected PacketPlayOutEntityDestroy init() {
        return new PacketPlayOutEntityDestroy(entityIDs);
    }
}
