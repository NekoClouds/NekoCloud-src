package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;

public interface PacketEntityLook extends DPacketEntity<DEntity> {

    void setPitch(byte pitch);

    void setYaw(byte yaw);

}
