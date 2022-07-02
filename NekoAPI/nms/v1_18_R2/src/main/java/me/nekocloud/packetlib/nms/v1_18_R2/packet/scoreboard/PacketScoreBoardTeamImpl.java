package me.nekocloud.packetlib.nms.v1_12_R1.packet.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreBoardTeam;
import me.nekocloud.packetlib.nms.scoreboard.DTeam;
import me.nekocloud.packetlib.nms.scoreboard.TeamAction;
import me.nekocloud.packetlib.nms.util.ReflectionUtils;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.DPacketBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardTeam;

@Getter
@AllArgsConstructor
public class PacketScoreBoardTeamImpl extends DPacketBase<PacketPlayOutScoreboardTeam> implements PacketScoreBoardTeam {

    private DTeam team;
    private TeamAction teamAction;

    @Override
    public void setTeam(DTeam team) {
        this.team = team;
        init();
    }

    @Override
    public void setTeamAction(TeamAction action) {
        this.teamAction = action;
        init();
    }

    @Override
    protected PacketPlayOutScoreboardTeam init() {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        ReflectionUtils.setFieldValue(packet, "a", team.getName());
        ReflectionUtils.setFieldValue(packet, "e", team.getVisibility().getValue());

        ReflectionUtils.setFieldValue(packet, "i", teamAction.getMode());
        ReflectionUtils.setFieldValue(packet, "j", team.packOptionData());
        ReflectionUtils.setFieldValue(packet, "g", 0);

        ReflectionUtils.setFieldValue(packet, "h", team.getPlayers());

        if (teamAction == TeamAction.CREATE || teamAction == TeamAction.UPDATE) {
            ReflectionUtils.setFieldValue(packet, "b", team.getDisplayName());
            ReflectionUtils.setFieldValue(packet, "c", team.getPrefix());
            ReflectionUtils.setFieldValue(packet, "d", team.getSuffix());

            ReflectionUtils.setFieldValue(packet, "f", team.getCollides().getValue());
        }

        return packet;
    }
}
