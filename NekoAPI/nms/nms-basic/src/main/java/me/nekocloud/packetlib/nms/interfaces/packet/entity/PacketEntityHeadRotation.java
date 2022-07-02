package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;

public interface PacketEntityHeadRotation extends DPacketEntity<DEntity> {

    void setYaw(byte yaw);
}
