package me.nekocloud.packetlib.libraries.scoreboard.objective;

import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketDisplayObjective;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreboardObjective;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.scoreboard.DisplaySlot;
import me.nekocloud.api.scoreboard.Objective;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.scoreboard.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CraftObjective implements Objective {
    private static final PacketContainer PACKET_CONTAINER = NmsAPI.getManager().getPacketContainer();

    private final ObjectiveManager objectiveManager;

    private final DObjective objective;

    private DisplaySlot slot;
    private boolean vision;
    private Player owner;

    private final List<String> players = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Integer> scores = new ConcurrentHashMap<>();

    public CraftObjective(ObjectiveManager objectiveManager, String name, String value, ObjectiveType type) {
        this.objectiveManager = objectiveManager;
        this.objective = new DObjective(name, value, type);

        this.slot = DisplaySlot.BELOW_NAME;
        this.vision = false;
        this.owner = null;

        this.objectiveManager.addObjective(this);
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public Collection<String> getVisiblePlayers() {
        if (vision) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }

        return players;
    }


    @Override
    public void showTo(Player player) {
        String name = player.getName().toLowerCase();
        if (players.contains(name)) {
            return;
        }

        players.add(name);

        PacketScoreboardObjective packet1 = PACKET_CONTAINER.getScoreboardObjectivePacket(objective,
                ObjectiveActionMode.CREATE);
        PacketDisplayObjective packet2 = PACKET_CONTAINER.getDisplayObjectivePacket(slot, objective);
        PACKET_CONTAINER.sendPacket(player, packet1, packet2);
    }

    @Override
    public void showTo(BukkitGamer gamer) {
        if (gamer == null) {
            return;
        }

        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        showTo(player);
    }

    @Override
    public void removeTo(BukkitGamer gamer) {
        if (gamer == null) {
            return;
        }

        Player player = gamer.getPlayer();
        if (player == null) { //нельзя проверять на getOnline, тк это при выходе игрока делается и будет true
            return;
        }

        removeTo(player);
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return players.contains(player.getName().toLowerCase());
    }

    @Override
    public void removeTo(Player player) {
        if (!players.remove(player.getName().toLowerCase())) {
            return;
        }

        PACKET_CONTAINER.getScoreboardObjectivePacket(objective, ObjectiveActionMode.REMOVE).sendPacket(player);
    }

    @Override
    public void hideAll() {
        setPublic(true);
        Bukkit.getOnlinePlayers().forEach(this::removeTo);
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
        showTo(owner);
    }

    public DObjective getDObjective() {
        return objective;
    }

    @Override
    public boolean isPublic() {
        return vision;
    }

    @Override
    public void setPublic(boolean vision) {
        this.vision = vision;

        if (vision) {
            PacketScoreboardObjective packet1 = PACKET_CONTAINER.getScoreboardObjectivePacket(objective,
                    ObjectiveActionMode.CREATE);
            PacketDisplayObjective packet2 = PACKET_CONTAINER.getDisplayObjectivePacket(slot, objective);
            sendPacket(packet1);
            sendPacket(packet2);
            return;
        }

        PacketScoreboardObjective packet = PACKET_CONTAINER.getScoreboardObjectivePacket(objective,
                ObjectiveActionMode.REMOVE);
        Bukkit.getOnlinePlayers().forEach(p -> {
            String name = p.getName().toLowerCase();
            if (!players.contains(name)) {
                packet.sendPacket(p);
            }

        });
    }

    @Override
    public void setDisplayName(String displayName) {
        objective.setDisplayName(displayName);
        sendPacket(PACKET_CONTAINER.getScoreboardObjectivePacket(objective,
                ObjectiveActionMode.UPDATE));
    }

    @Override
    public void setDisplaySlot(DisplaySlot displaySlot) {
        slot = displaySlot;
        sendPacket(PACKET_CONTAINER.getDisplayObjectivePacket(displaySlot, objective));
    }

    @Override
    public void setScore(Player player, int score) {
        String name = player.getName().toLowerCase();
        if (!vision && !isVisibleTo(player)) {
            return;
        }

        scores.put(name, score);
        DScore dScore = new DScore(player.getName(), objective, score);
        sendPacket(PACKET_CONTAINER.getScoreboardScorePacket(dScore,
                ScoreboardAction.CHANGE));
    }

    @Override
    public Map<String, Integer> getScores() {
        return Collections.unmodifiableMap(scores);
    }

    @Override
    public void removeScore(String name) {
        Integer score = scores.remove(name.toLowerCase());
        if (score == null) {
            return;
        }

        //мб отправлять, что у этого игрока 0 очков, но я не уверен
    }

    @Override
    public void removeScore(Player player) {
        removeScore(player.getName());
    }

    @Override
    public void remove() {
        objectiveManager.removeObjective(this);
        PacketScoreboardObjective packet = PACKET_CONTAINER.getScoreboardObjectivePacket(objective,
                ObjectiveActionMode.REMOVE);
        sendPacket(packet);
    }

    private void sendPacket(DPacket packet) {
        if (vision) {
            Bukkit.getOnlinePlayers().forEach(packet::sendPacket);
            return;
        }
        players.forEach(name -> packet.sendPacket(Bukkit.getPlayerExact(name)));
    }
}
