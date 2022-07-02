package me.nekocloud.packetlib.libraries.entity.npc;

import io.netty.util.internal.ConcurrentSet;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreBoardTeam;
import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.scoreboard.DTeam;
import me.nekocloud.packetlib.nms.scoreboard.TagVisibility;
import me.nekocloud.packetlib.nms.scoreboard.TeamAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NPCManager {

    private final PacketContainer packetContainer = NmsAPI.getManager().getPacketContainer();

    private final Map<String, Set<Integer>> playersTeam = new ConcurrentHashMap<>();
    private final Map<Integer, CraftNPC> npcList = new ConcurrentHashMap<>();

    public void addPlayerToTeam(Player player, NPC npc, ChatColor chatColor) {
        String name = player.getName().toLowerCase();
        Set<Integer> npcs = playersTeam.computeIfAbsent(name, s -> new ConcurrentSet<>());
        if (npcs.contains(npc.getEntityID()))
            return;

        npcs.add(npc.getEntityID());
        createTeam(player, npc, chatColor);
    }

    void removeFromTeams(Player player) {
        playersTeam.remove(player.getName().toLowerCase());
    }

    public void addNPC(CraftNPC npc) {
        if (npc.entity == null)
            return;

        npcList.put(npc.getEntityID(), npc);
    }

    public Map<Integer, CraftNPC> getNPCs() {
        return npcList;
    }

    void removeNPC(CraftNPC npc) {
        npcList.remove(npc.getEntityID());
    }

    /*
     * createTeam используется для того, чтобы зашедшему игроку
     * отосласть пакет создания скорборда. Сразу, после респавна НПС, игроку
     * нужно отослать пакет с помощью addPlayer, что этот НПС(заспавненный) находится в этой тиме
     */
    private void createTeam(Player player, NPC npc, ChatColor chatColor) {
        DTeam team = new DTeam(npc.getName() + "_TEAM");
        team.setVisibility(TagVisibility.NEVER);
        team.setPrefix(chatColor.toString());
        PacketScoreBoardTeam packet = packetContainer.getScoreBoardTeamPacket(team, TeamAction.CREATE);
        packet.sendPacket(player);
    }
}
