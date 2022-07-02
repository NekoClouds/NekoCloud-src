package me.nekocloud.packetlib.libraries.scoreboard.board;

import me.nekocloud.api.scoreboard.Board;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BoardManager {

    private final Map<String, CraftBoard> boards = new ConcurrentHashMap<>();

    void addBoard(Player player, CraftBoard board){
        String name = player.getName().toLowerCase();
        Board check = boards.remove(name);
        if (check != null)
            check.remove();

        boards.put(name, board);
    }

    void removeBoard(String name) {
        boards.remove(name.toLowerCase());
    }

    public Map<String, CraftBoard> getPlayersBoard() {
        return boards;
    }
}
