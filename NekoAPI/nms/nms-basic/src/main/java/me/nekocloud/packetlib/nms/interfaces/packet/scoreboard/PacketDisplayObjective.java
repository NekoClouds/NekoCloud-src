package me.nekocloud.packetlib.nms.interfaces.packet.scoreboard;

import me.nekocloud.api.scoreboard.DisplaySlot;
import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.scoreboard.DObjective;

public interface PacketDisplayObjective extends DPacket {

    void setObjective(DObjective objective);

    DObjective getObjective();

    void setDisplaySlot(DisplaySlot slot);

    DisplaySlot getSlot();
}
