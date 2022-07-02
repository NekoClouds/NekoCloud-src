package me.nekocloud.packetlib.nms.interfaces.packet.scoreboard;

import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.scoreboard.DObjective;
import me.nekocloud.packetlib.nms.scoreboard.ObjectiveActionMode;

public interface PacketScoreboardObjective extends DPacket {

    void setObjective(DObjective objective);

    DObjective getObjective();

    void setMode(ObjectiveActionMode mode);

    ObjectiveActionMode getMode();
}
