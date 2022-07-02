package me.nekocloud.nekoapi.stats.playtime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public final class PlayTimeManager {

    NekoAPI nekoAPI;

    GamerManager gamerManager = NekoCloud.getGamerManager();

    /*
    private @Getter final Int2LongMap playerOnlineTime = new Int2LongOpenHashMap();
    private @Getter final Int2LongMap lastCheckedTime = new Int2LongOpenHashMap();
    */

    Map<Integer, Long> playerOnlineTime = new HashMap<>();
    Map<Integer, Long> lastCheckedTime = new HashMap<>();

    public PlayTimeManager(@NonNull NekoAPI nekoAPI) {
        this.nekoAPI = nekoAPI;
        new PlayTimeListener(this);

        StatsLoader.init();

        for (val gamer : gamerManager.getGamers().values()) {
            if (gamer == null)
                return;
            val onlineTime = StatsLoader.getTime(gamer.getPlayerID());

            playerOnlineTime.put(gamer.getPlayerID(), onlineTime);
            lastCheckedTime.put(gamer.getPlayerID(), System.currentTimeMillis());
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(nekoAPI, () -> {
            for (val gamer : gamerManager.getGamers().values()) {
                if (gamer == null)
                    return;
                update(gamer.getPlayerID(), false);
            }
        }, 0, 100L);
    }

    public void disable() {
        if (nekoAPI == null)
            return;
        for (val gamer : gamerManager.getGamers().values()) {
            if (gamer == null)
                return;
            forceSave(gamer.getPlayerID());
        }

        playerOnlineTime.clear();
        lastCheckedTime.clear();

    }

    public void update(int id, boolean save) {
        val extraTime = System.currentTimeMillis() - lastCheckedTime.get(id);
        val newTime = playerOnlineTime.get(id) + extraTime;

        lastCheckedTime.replace(id, System.currentTimeMillis());
        playerOnlineTime.replace(id, newTime);

        if (save) StatsLoader.saveData(id, playerOnlineTime.get(id));
    }

    public void forceSave(int id) {
        val extraTime = System.currentTimeMillis() - lastCheckedTime.get(id);
        lastCheckedTime.replace(id, System.currentTimeMillis());

        val newTime = playerOnlineTime.get(id) + extraTime;
        playerOnlineTime.replace(id, newTime);

        StatsLoader.saveData(id, playerOnlineTime.get(id));
    }

    /**
     * Получить наигранное время из мапы
     * @param id ид игрока
     * @return наигранное время
     */
    public long getTimeFromMap(int id) {
        val extraTime = System.currentTimeMillis() - lastCheckedTime.get(id);
        lastCheckedTime.replace(id, System.currentTimeMillis());

        val newTime = playerOnlineTime.get(id) + extraTime;
        playerOnlineTime.replace(id, newTime);

        return playerOnlineTime.get(id);
    }
}
