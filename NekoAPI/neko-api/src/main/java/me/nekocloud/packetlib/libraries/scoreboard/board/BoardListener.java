package me.nekocloud.packetlib.libraries.scoreboard.board;

import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.packetlib.libraries.scoreboard.ScoreBoardAPIImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class BoardListener extends DListener<NekoAPI> {

    private final ScoreBoardAPIImpl scoreBoardAPI;

    public BoardListener(ScoreBoardAPIImpl scoreBoardAPI) {
        super(scoreBoardAPI.getNekoAPI());
        this.scoreBoardAPI = scoreBoardAPI;
        BoardManager boardManager = scoreBoardAPI.getBoardManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(AsyncGamerQuitEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }
        scoreBoardAPI.removeBoard(player);
    }
}
