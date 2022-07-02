package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.api.entity.npc.AnimationNpcType;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;

public interface PacketAnimation extends DPacketEntity<DEntity> {

    AnimationNpcType getAnimation();

    void setAnimation(AnimationNpcType animation);
}
