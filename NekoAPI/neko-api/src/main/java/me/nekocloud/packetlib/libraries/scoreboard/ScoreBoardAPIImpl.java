package me.nekocloud.packetlib.libraries.scoreboard;

import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.scoreboard.*;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.packetlib.libraries.scoreboard.board.BoardListener;
import me.nekocloud.packetlib.libraries.scoreboard.board.BoardManager;
import me.nekocloud.packetlib.libraries.scoreboard.board.BoardTask;
import me.nekocloud.packetlib.libraries.scoreboard.board.CraftBoard;
import me.nekocloud.packetlib.libraries.scoreboard.objective.CraftObjective;
import me.nekocloud.packetlib.libraries.scoreboard.objective.ObjectiveListener;
import me.nekocloud.packetlib.libraries.scoreboard.objective.ObjectiveManager;
import me.nekocloud.packetlib.libraries.scoreboard.tag.CraftTag;
import me.nekocloud.packetlib.nms.scoreboard.ObjectiveType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBoardAPIImpl implements ScoreBoardAPI {

    @Getter
    private final NekoAPI nekoAPI;

    private Objective tabObjective;
    private org.bukkit.scoreboard.Objective healthObjective;

    private final Map<String, PlayerTag> tags = new ConcurrentHashMap<>();

    @Getter
    private final BoardManager boardManager;
    @Getter
    private final ObjectiveManager objectiveManager;

    public ScoreBoardAPIImpl(NekoAPI nekoAPI) {
        this.nekoAPI = nekoAPI;

        boardManager = new BoardManager();
        objectiveManager = new ObjectiveManager();

        new BoardListener(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(nekoAPI, new BoardTask(boardManager), 0, 1L);
        //ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(new BoardTask(boardManager), 0, 50, TimeUnit.MILLISECONDS);

        new ObjectiveListener(this);
    }

    @Override
    public Board createBoard() {
        return new CraftBoard(boardManager);
    }

    @Override
    public Objective createObjective(String name, String value) {
        return new CraftObjective(objectiveManager, name, value, ObjectiveType.INTEGER);
    }

    @Override
    public PlayerTag createTag(String name) {
        return new CraftTag(name);
    }

    @Override
    public void createGameObjective(boolean health, boolean tab) {
        if (health) {
            if (healthObjective != null)
                healthObjective.unregister();

            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            healthObjective = scoreboardManager.getMainScoreboard().registerNewObjective("showhealth", "health");
            healthObjective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.BELOW_NAME);
            healthObjective.setDisplayName("§4❤");

            Bukkit.getOnlinePlayers().forEach(player -> healthObjective
                    .getScore(player.getName())
                    .setScore((int) player.getHealth()));
        }

        if (tab) {
            if (tabObjective != null)
                tabObjective.remove();

            tabObjective = createObjective("playerlist", "stat");
            tabObjective.setDisplaySlot(DisplaySlot.LIST);
            tabObjective.setPublic(true);
        }
    }

    @Override
    public void setScoreTab(Player player, int score) {
        if (player == null || tabObjective == null) {
            return;
        }

        tabObjective.setScore(player, score);
    }

    @Override
    public void unregisterObjectives() {
        if (tabObjective != null) {
            tabObjective.remove();
            tabObjective = null;
        }

        if (healthObjective != null) {
            healthObjective.unregister();
            healthObjective = null;
        }
    }

    @Override
    public Map<String, PlayerTag> getActiveDefaultTag() {
        return Collections.unmodifiableMap(tags);
    }

    @Override
    public void setDefaultTag(Player player, PlayerTag playerTag) {
        tags.values().forEach(tag -> tag.sendTo(player));
        tags.put(player.getName(), playerTag);
        playerTag.sendToAll();
    }

    @Override
    public void removeDefaultTag(Player player) {
        PlayerTag playerTag = tags.remove(player.getName());
        if (playerTag == null) {
            return;
        }

        playerTag.remove();
    }

    @Override
    public void removeDefaultTags() {
        tags.values().forEach(PlayerTag::remove);
        tags.clear();
    }

    @Override
    public void setPrefix(Player player, String prefix) {
        PlayerTag playerTag = tags.get(player.getName());
        if (playerTag == null) {
            return;
        }

        playerTag.setPrefix(prefix);
        if (NekoCloud.isGame()) {
            return;
        }

        playerTag.sendToAll();
    }

    @Override
    public void setSuffix(Player player, String suffix) {
        PlayerTag playerTag = tags.get(player.getName());
        if (playerTag == null) {
            return;
        }

        playerTag.setSuffix(suffix);
        if (NekoCloud.isGame()) {
            return;
        }

        playerTag.sendToAll();
    }

    @Override
    public void removeBoard(Player player) {
        Board board = getBoard(player);
        if (board == null) {
            return;
        }

        board.remove();
    }

    @Override
    public Board getBoard(Player player) {
        return boardManager.getPlayersBoard().get(player.getName().toLowerCase());
    }

    @Override
    public Map<String, Board> getActiveBoards() {
        return Collections.unmodifiableMap(boardManager.getPlayersBoard());
    }

    @Override
    public int getPriorityScoreboardTag(Group group) {
        switch (group) {
            case OWNER:
            case DEVELOPER:
            case ADMIN:
                return 0;
            case SRMODERATOR:
                return 100;
            case MODERATOR:
                return 200;
            case SRBUILDER:
            case BUILDER:
                return 300;
            case JUNIOR:
                return 400;
            case TIKTOK:
            case YOUTUBE:
                return 500;
            case NEKO:
                return 600;
            case AXSIDE:
                return 700;
            case TRIVAL:
                return 800;
            case AKIO:
                return 900;
            case HEGENT:
                return 950;
            default:
                return 960;
        }
    }
}
