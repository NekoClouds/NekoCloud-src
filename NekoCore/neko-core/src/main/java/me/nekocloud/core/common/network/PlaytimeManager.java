package me.nekocloud.core.common.network;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import me.nekocloud.base.sql.PlayerInfoLoader;

public class PlaytimeManager {

    public static final PlaytimeManager INSTANCE = new PlaytimeManager();

    private static final String INJECT_QUERY = "SELECT * FROM `player_stats` WHERE `playerID`=?";

    private final Int2LongMap playerPlaytimeMap = new Int2LongOpenHashMap();

    public long getPlayTime(int playerID) {
        PlayerInfoLoader.getMysqlDatabase().executeQuery(INJECT_QUERY, resultSet -> {
            if (!resultSet.next())
                return null;

            playerPlaytimeMap.put(playerID, resultSet.getLong("playtime"));
            return null;

         }, playerID);

        return playerPlaytimeMap.get(playerID);
    }
}
