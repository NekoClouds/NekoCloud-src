package me.nekocloud.chat.core.manager;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.core.CoreSql;

@Getter
public class IgnoreManager {

	MySqlDatabase database;

    public IgnoreManager() {
        database = CoreSql.getDatabase();
		initTables();
    }

    public IntList getIgnoreList(int playerID) {
        return database.executeQuery(
				"SELECT `ignored_id` FROM `players_ignored` WHERE `player_id` = ?;", rs -> {
            IntList ignoreList = new IntArrayList();

            while (rs.next()) {
                ignoreList.add(rs.getInt("ignored_id"));
            }

            return ignoreList;
        }, playerID);
    }

    public void addIgnore(int playerID, int ignoredID) {
        database.execute("INSERT INTO `players_ignored` (`player_id`, `ignored_id`) VALUES (?, ?);",
				playerID, ignoredID);
    }

    public void removeIgnore(int playerID, int ignoreID) {
        database.execute("DELETE FROM `players_ignored` WHERE `player_id` = ? AND `ignored_id` = ? LIMIT 1;",
				playerID, ignoreID);
    }

    private void initTables() {
        new TableConstructor("players_ignored",
                new TableColumn("player_id", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("ignored_id", ColumnType.INT_11)
        ).create(database);
    }

}
