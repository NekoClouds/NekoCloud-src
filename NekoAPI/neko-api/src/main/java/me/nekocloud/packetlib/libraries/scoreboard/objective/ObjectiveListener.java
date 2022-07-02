package me.nekocloud.packetlib.libraries.scoreboard.objective;

import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.packetlib.libraries.scoreboard.ScoreBoardAPIImpl;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.scoreboard.DScore;
import me.nekocloud.packetlib.nms.scoreboard.ScoreboardAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ObjectiveListener extends DListener<NekoAPI> {

    private final PacketContainer packetContainer = NmsAPI.getManager().getPacketContainer();
    private final ObjectiveManager objectiveManager;

    public ObjectiveListener(ScoreBoardAPIImpl scoreBoardAPI) {
        super(scoreBoardAPI.getNekoAPI());
        this.objectiveManager = scoreBoardAPI.getObjectiveManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(AsyncGamerJoinEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        for (CraftObjective craftObjective : objectiveManager.getObjectives()) {
            if (craftObjective.isPublic()) {
                craftObjective.showTo(player);
            }

            for (String name : craftObjective.getScores().keySet()) {
                Player all = Bukkit.getPlayer(name);
                if (all == null || !all.isOnline()) {
                    continue;
                }

                Integer score = craftObjective.getScores().get(name);
                if (score == null) {
                    continue;
                }

                DScore dScore = new DScore(all.getName(), craftObjective.getDObjective(), score);
                packetContainer.getScoreboardScorePacket(dScore, ScoreboardAction.CHANGE).sendPacket(player);
            }
        }
    }

    @EventHandler
    public void onQuit(AsyncGamerQuitEvent e) {
        String name = e.getGamer().getName();

        for (CraftObjective craftObjective : objectiveManager.getObjectives()) {
            if (craftObjective.getOwner() != null && craftObjective.getOwner().getName().equalsIgnoreCase(name)) {
                craftObjective.remove();
                continue;
            }

            craftObjective.removeScore(name);
            craftObjective.removeTo(e.getGamer());
        }
    }
}
