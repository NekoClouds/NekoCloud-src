package me.nekocloud.games.jobs.data;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public final class JobsStatsLoader {

   @Getter
   private static MySqlDatabase mySql;

   public static void init(MySqlDatabase mySql) {
      JobsStatsLoader.mySql = mySql;
      createTables();
   }

   private static void createTables() {
      new TableConstructor("jobs_stats",
              new TableColumn("player_id", ColumnType.INT_11).primaryKey(true).unigue(true).setDefaultValue(-1),
              new TableColumn("mines_earned", ColumnType.INT_11).setDefaultValue(0),
              new TableColumn("woods_earned", ColumnType.INT_11).setDefaultValue(0)).create(mySql);
   }

   public static void addValue(int playerID, int value, boolean mines) {
      mySql.execute("INSERT INTO `jobs_stats` (`player_id`, `mines_earned`, `woods_earned`) VALUES ("
              + playerID + ", " + (mines ? value + ", 0" : "0, " + value) + ") ON DUPLICATE KEY UPDATE "
              + (mines ? "`mines_earned` = `mines_earned` + " + value : "`woods_earned` = `woods_earned` + "
              + value) + "");
   }

   public static Map<IBaseGamer, Integer> getTop(boolean mines, int limit) {
      return mySql.executeQuery("SELECT * FROM jobs_stats ORDER BY " +
              (mines ? "mines_earned" : "woods_earned") + " DESC LIMIT " + limit, (rs) -> {
         Map<IBaseGamer, Integer> top = new LinkedHashMap<>();

         while(rs.next()) {
            if (rs.getInt(1) != -1) {
               IBaseGamer baseGamer = GamerAPI.getById(rs.getInt(1));
               if (baseGamer != null) {
                  top.put(baseGamer, rs.getInt(mines ? 2 : 3));
               }
            }
         }

         return top;
      });
   }
}
