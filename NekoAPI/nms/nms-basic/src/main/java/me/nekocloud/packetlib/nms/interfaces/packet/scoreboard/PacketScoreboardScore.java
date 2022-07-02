package me.nekocloud.packetlib.nms.interfaces.packet.scoreboard;

import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.scoreboard.DScore;
import me.nekocloud.packetlib.nms.scoreboard.ScoreboardAction;

public interface PacketScoreboardScore extends DPacket {

    DScore getScore();

    void setScore(DScore score);

    ScoreboardAction getAction();

    void setAction(ScoreboardAction action);
}
