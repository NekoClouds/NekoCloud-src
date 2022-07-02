package me.nekocloud.packetlib.libraries.scoreboard.board;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.scoreboard.Board;
import me.nekocloud.api.scoreboard.BoardLine;
import me.nekocloud.api.scoreboard.DisplaySlot;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.nekoapi.utils.ViaUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.libraries.scoreboard.board.lines.CraftBoardLine;
import me.nekocloud.packetlib.libraries.scoreboard.board.lines.DisplayNameLine;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketDisplayObjective;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreBoardTeam;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreboardObjective;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreboardScore;
import me.nekocloud.packetlib.nms.scoreboard.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Written by _Novit_ 23.01.22
 */
public class CraftBoard implements Board {

    private static final PacketContainer PACKET_CONTAINER = NmsAPI.getManager().getPacketContainer();

    private final BoardManager boardManager;
    @Getter
    private final DisplayNameLine displayNameLine;
    @Getter
    private final Map<Runnable, Long> tasks = new ConcurrentHashMap<>();

    @Getter
    private Player owner;
    private boolean active;

    public final Map<Integer, CraftBoardLine> lines = new ConcurrentHashMap<>();

    public CraftBoard(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.displayNameLine = new DisplayNameLine();
        this.active = false;
    }

    @Override
    public void showTo(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Version ver = ViaUtil.getVersion(player);
        owner = player;
        boardManager.addBoard(player, this);
        active = true;

        PacketScoreboardObjective packet1 = PACKET_CONTAINER.getScoreboardObjectivePacket(
                displayNameLine.getdObjective(), ObjectiveActionMode.CREATE);
        PacketDisplayObjective packet2 = PACKET_CONTAINER.getDisplayObjectivePacket(
                DisplaySlot.SIDEBAR, displayNameLine.getdObjective());

        PACKET_CONTAINER.sendPacket(player, packet1, packet2);

        displayNameLine.setPlayerName(player);

        //fix >1.13 client board
        lines.forEach((integer, craftBoardLine) -> {
            if (craftBoardLine.getTeam() != null && ver.getProtocol() >= Version.V_1_13.getProtocol()) {
                DTeam dTeam = craftBoardLine.getTeam();

                craftBoardLine = new CraftBoardLine(this, craftBoardLine.getNumber(),
                        dTeam.getPrefix() + dTeam.getSuffix() + craftBoardLine.getText(), craftBoardLine.isDynamic());
            }

            sendLine(craftBoardLine, integer, true);

        });
        BukkitUtil.runTaskAsync(() -> {
            for (Runnable runnable : getTasks().keySet()) {
                runnable.run();
            }
        });
    }

    @Override
    public void showTo(BukkitGamer gamer) {
        showTo(gamer.getPlayer());
    }

    public boolean isActive() {
        return active && owner != null && owner.isOnline();
    }

    public void sendLine(CraftBoardLine craftBoardLine, int number, boolean sendTeam) {
        if (owner == null)
            return;

        DScore dScore = new DScore(craftBoardLine.getText(), displayNameLine.getdObjective(), number);

        PacketScoreboardScore packet = PACKET_CONTAINER.getScoreboardScorePacket(dScore,
                ScoreboardAction.CHANGE);
        packet.sendPacket(owner);

        if (craftBoardLine.getTeam() != null && sendTeam) {
            DTeam team = craftBoardLine.getTeam();
            PacketScoreBoardTeam packet2 = PACKET_CONTAINER.getScoreBoardTeamPacket(team, TeamAction.CREATE);
            packet2.sendPacket(owner);
        }
    }

    @Override
    public Map<Integer, BoardLine> getLines() {
        return new HashMap<>(lines);
    }

    @Override
    public int getSize() {
        return lines.size();
    }

    @Override
    public void setDisplayName(String name) {
        displayNameLine.setName(name);
    }

    @Override
    public void setDynamicDisplayName(String name) {
        displayNameLine.setNames(name);
        displayNameLine.setSpeed(1);
    }

    @Override
    public void setDynamicDisplayName(Collection<String> lines, long speed) {
        List<String> line = new ArrayList<>(lines);
        if (line.size() == 0) {
            throw new IllegalStateException("Список анимаций не может быть пустым!");
        }

        displayNameLine.setNames(line, line.get(0));
        displayNameLine.setSpeed(speed);
    }

    @Override
    public void setLine(int number, String line) {
        if (line == null) {
            line = "§cError";
        }

        CraftBoardLine craftBoardLine = lines.get(number);
        if (craftBoardLine != null) {
            removeLine(number);
        }

        boolean easy = line.length() <= 16;

        if (easy) {
            craftBoardLine = new CraftBoardLine(this, number, line, false);
        } else {
            String[] s = BoardUtil.parse(line);
            DTeam team = new DTeam(String.valueOf(number));
            team.setPrefix(s[0]);
            team.addPlayer(s[1]);
            team.setSuffix(s[2]);
            craftBoardLine = new CraftBoardLine(this, number, s[1], false, team);
        }

        sendLine(craftBoardLine, number, true);
        lines.put(number, craftBoardLine);
    }

    @Override
    public void setDynamicLine(int number, String notChangeText, String change) {
        if (notChangeText.length() > 32) {
            throw new IllegalStateException("Text value too big");
        }

        if (notChangeText.length() > 16) {
            createDynamicLine(number, notChangeText.substring(0, 16), notChangeText.substring(16), change);
        } else {
            createDynamicLine(number, "", notChangeText, change);
        }

    }

    public void createDynamicLine(int number, String pref, String name, String suffix) {
        CraftBoardLine craftBoardLine = lines.get(number);
        PacketScoreBoardTeam packet;

        if (craftBoardLine == null || !craftBoardLine.getText().equals(name)) {
            if (craftBoardLine != null) {
                removeLine(number);
            }
            //fix >1.13 client board
            if(owner != null && ViaUtil.getVersion(owner).getProtocol() >= Version.V_1_13.getProtocol()) {
                craftBoardLine = new CraftBoardLine(this, number, pref + name + suffix, true);
                lines.put(number, craftBoardLine);
                sendLine(craftBoardLine, number, false);
                return;
            }

            craftBoardLine = new CraftBoardLine(this, number, name, true);
            lines.put(number, craftBoardLine);
            sendLine(craftBoardLine, number, false);

            DTeam team = new DTeam(String.valueOf(number));
            team.addPlayer(name);
            team.setPrefix(pref);
            team.setSuffix(suffix);
            craftBoardLine.setTeam(team);

            packet = PACKET_CONTAINER.getScoreBoardTeamPacket(team, TeamAction.CREATE);
        } else {
            DTeam team = craftBoardLine.getTeam();
            if (craftBoardLine.getTeam() == null) {
                team = new DTeam(String.valueOf(number));
                team.addPlayer(name);
                craftBoardLine.setTeam(team);
            }
            team.setPrefix(pref);
            team.setSuffix(suffix);

            packet = PACKET_CONTAINER.getScoreBoardTeamPacket(team, TeamAction.UPDATE);
        }

        if (owner == null)
            return;

        packet.sendPacket(owner);
    }

    @Override
    public void updater(Runnable runnable, long speed) {
        tasks.put(runnable, speed);
    }

    @Override
    public void updater(Runnable runnable) {
        updater(runnable, 20);
    }

    @Override
    public void removeUpdater(Runnable runnable) {
        tasks.remove(runnable);
    }

    @Override
    public void removeLine(int number) {
        CraftBoardLine craftBoardLine = lines.remove(number);
        if (craftBoardLine == null)
            return;

        DScore dScore = new DScore(craftBoardLine.getText(), displayNameLine.getdObjective(), number);
        PacketScoreboardScore packet = PACKET_CONTAINER.getScoreboardScorePacket(dScore,
                ScoreboardAction.REMOVE);

        if (owner == null)
            return;

        packet.sendPacket(owner);

        if (craftBoardLine.getTeam() == null)
            return;

        PacketScoreBoardTeam teamPacket = PACKET_CONTAINER.getScoreBoardTeamPacket(craftBoardLine.getTeam(),
                TeamAction.REMOVE);
        teamPacket.sendPacket(owner);
    }

    @Override
    public void remove() {
        if (owner == null)
            return;

        boardManager.removeBoard(owner.getName());
        active = false;

        for (int line : lines.keySet())
            removeLine(line);

        DObjective objective = displayNameLine.getdObjective();
        PacketScoreboardObjective packet = PACKET_CONTAINER.getScoreboardObjectivePacket(objective,
                ObjectiveActionMode.REMOVE);
        packet.sendPacket(owner);
    }
}
