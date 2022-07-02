package me.nekocloud.nekoapi.stats.playtime;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.PlayerInfoLoader;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
public final class StatsLoader {

    String GET_PLAYTIME_QUERY = "SELECT * FROM `player_stats` WHERE `playerID`= ? LIMIT 1;";
    String INS_PLAYTIME_QUERY = "INSERT INTO `player_stats` (`playerID`, `playtime`) VALUES (?, 0);";
    String UPD_PLAYTIME_QUERY = "UPDATE `player_stats` SET `playtime`= ? WHERE `playerID`= ? LIMIT 1;";

    MySqlDatabase MYSQL_DATABASE = PlayerInfoLoader.getMysqlDatabase();

    public static long getTime(int playerID) {
        return MYSQL_DATABASE.executeQuery(GET_PLAYTIME_QUERY, (rs) -> {
            long playtime = 0;

            if (rs.next()) {
                playtime = rs.getLong("playtime");
            } else {
                MYSQL_DATABASE.execute(INS_PLAYTIME_QUERY,
                        playerID);
            }

            return playtime;
        }, playerID);
    }

    public static void saveData(int playerID, long playtime) {
        MYSQL_DATABASE.execute(UPD_PLAYTIME_QUERY,
                playtime, playerID);
    }

    public void init() {
        new TableConstructor("player_stats",
                new TableColumn("playerID", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("playtime", ColumnType.BIG_INT)
        ).create(MYSQL_DATABASE);
    }
}
