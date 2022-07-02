package me.nekocloud.packetlib.nms.interfaces.packet.scoreboard;

import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.scoreboard.DTeam;
import me.nekocloud.packetlib.nms.scoreboard.TeamAction;

public interface PacketScoreBoardTeam extends DPacket {

    void setTeam(DTeam team);
    DTeam getTeam();

    void setTeamAction(TeamAction action);
    TeamAction getTeamAction();
}
