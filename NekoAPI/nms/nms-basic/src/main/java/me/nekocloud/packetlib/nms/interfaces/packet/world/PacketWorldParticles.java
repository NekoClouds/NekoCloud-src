package me.nekocloud.packetlib.nms.interfaces.packet.world;

import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;

public interface PacketWorldParticles extends DPacket {

    void setData(int[] data);
}
