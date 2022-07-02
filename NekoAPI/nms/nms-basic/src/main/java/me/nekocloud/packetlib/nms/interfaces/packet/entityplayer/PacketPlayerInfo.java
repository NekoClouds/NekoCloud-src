package me.nekocloud.packetlib.nms.interfaces.packet.entityplayer;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.DPacketEntity;
import me.nekocloud.packetlib.nms.types.PlayerInfoActionType;

public interface PacketPlayerInfo extends DPacketEntity<DEntityPlayer> {

    void setPlayerInfoAction(PlayerInfoActionType actionType);

    PlayerInfoActionType getActionType();
}
