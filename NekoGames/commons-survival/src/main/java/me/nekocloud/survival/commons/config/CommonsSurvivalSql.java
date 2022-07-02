package me.nekocloud.survival.commons.config;

import lombok.experimental.UtilityClass;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.*;
import me.nekocloud.survival.commons.object.CraftHome;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.survival.commons.object.CraftWarp;
import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class CommonsSurvivalSql {

    private final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private final MySqlDatabase MY_SQL_DATABASE = MySqlDatabase.newBuilder()
            .data(CommonsSurvival.getConfigData().getDataBase())
            .user("neko")
            .host(ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .create();

    public void saveData(int playerID, String column, String data) {
        MY_SQL_DATABASE.execute("UPDATE `Users` SET `" + column + "` = ? WHERE `playerID`= ?;", data, playerID);
    }

    public void setWarpPrivate(Warp warp) {
        MY_SQL_DATABASE.execute("UPDATE `Warps` SET `private` = ? WHERE `name`= ?;",
                warp.isPrivate(), warp.getName());
    }

    void loadWarps() {
        MY_SQL_DATABASE.executeQuery("SELECT * FROM `Warps`;", (rs) -> {
            while (rs.next()) {
                int owner = rs.getInt("playerID");
                String name = rs.getString("name");
                String stringLoc = rs.getString("location");
                boolean isPrivate = rs.getBoolean("private");
                Timestamp date = rs.getTimestamp("date");
                Warp warp = new CraftWarp(name, owner, stringLoc, date.getTime(), isPrivate);
                CommonsSurvivalAPI.getWarpManager().addWarp(warp);
            }
            return Void.TYPE;
        });
    }

    public User createUser(String name) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);

        int playerID;
        if (gamer == null) {
            playerID = GlobalLoader.containsPlayerID(name);
        } else {
            playerID = gamer.getPlayerID();
        }

        return MY_SQL_DATABASE.executeQuery("SELECT * FROM `Users` WHERE `PlayerID`= ? LIMIT 1;", (rs) -> {
            boolean first = false;

            boolean fly = false;
            boolean god = false;
            boolean tpToggle = true;

            Location lastLocation = null;
            Location bedLocation = null;

            Map<Kit, Timestamp> kits = new ConcurrentHashMap<>();
            Map<String, Home> homes = new ConcurrentHashMap<>();

            if (rs.next()) {
                fly = (rs.getInt("fly") == 1);
                god = (rs.getInt("god") == 1);
                tpToggle = (rs.getInt("tpToggle") == 1);
                String loc = rs.getString("lastLocation");
                String bedLoc = rs.getString("bedLocation");
                if (loc != null)
                    lastLocation = LocationUtil.stringToLocation(loc, true);

                if (bedLoc != null)
                    bedLocation = LocationUtil.stringToLocation(bedLoc, false);

            } else {
                first = true;
                MY_SQL_DATABASE.execute("INSERT INTO `Users` (`playerID`, `fly`, `god`, `tpToggle`) VALUES (? ,'0', '0', '1');", playerID);
            }

            MY_SQL_DATABASE.executeQuery("SELECT * FROM `Kits` WHERE `PlayerID`= ?;", (resultSet) -> {
                while (resultSet.next()) {
                    Timestamp time = resultSet.getTimestamp("date");
                    Kit kit = CommonsSurvivalAPI.getKitManager().getKit(resultSet.getString("kit"));
                    if (kit != null)
                        kits.put(kit, time);

                }
                return Void.TYPE;
            }, playerID);

            MY_SQL_DATABASE.executeQuery("SELECT * FROM `Homes` WHERE `PlayerID`= ?;", (resultSet) -> {
                while (resultSet.next()) {
                    String loc = resultSet.getString("location");
                    Location locHome = LocationUtil.stringToLocation(loc, true);
                    String nameLoc = resultSet.getString("name");
                    Home home = new CraftHome(nameLoc, locHome);
                    homes.put(nameLoc, home);
                }
                return Void.TYPE;
            }, playerID);

            return new CraftUser(name, fly, god, tpToggle, first, lastLocation, kits, homes, bedLocation);
        }, playerID);
    }

    public void addKitToSql(int playerID, String name, Long time) {
        MY_SQL_DATABASE.execute("INSERT INTO `Kits` (`playerID`, `kit`, `date`) VALUES (?, ?, ?);",
                playerID, name, new Timestamp(time));
    }

    public void removeKitFromSql(int playerID, Kit kit) {
        MY_SQL_DATABASE.execute("DELETE FROM `Kits` WHERE `kit` = ? && `playerID` = ?;", kit.getName(), playerID);
    }

    public void addHomeToSql(int playerID, String name, Location location) {
        String home = LocationUtil.locationToString(location, true);
        MY_SQL_DATABASE.execute("INSERT INTO `Homes` (`playerID`, `name`, `location`) VALUES (?, ?, ?);",
                playerID, name, home);
    }

    public void removeHomeFromSql(int playerID, String name) {
        MY_SQL_DATABASE.execute("DELETE FROM `Homes` WHERE `name` = ? && `playerID` = ?;", name, playerID);
    }

    public void addWarp(String name, int playerID, Location location) {
        String warpLocation = LocationUtil.locationToString(location, true);
        MY_SQL_DATABASE.execute("INSERT INTO `Warps` (`playerID`, `name`, `location`, `date`) VALUES (?, ?, ?, ?);",
                playerID, name, warpLocation, new Timestamp(System.currentTimeMillis()));
    }

    public void removeWarp(String name) {
        MY_SQL_DATABASE.execute("DELETE FROM `Warps` WHERE `name` = ?;", name);
    }

    public void init() {
        new TableConstructor("Users",
                new TableColumn("playerID", ColumnType.INT_11).primaryKey(true),
                new TableColumn("fly", ColumnType.BOOLEAN),
                new TableColumn("god", ColumnType.BOOLEAN),
                new TableColumn("tpToggle", ColumnType.BOOLEAN),
                new TableColumn("lastLocation", ColumnType.TEXT).setNull(true),
                new TableColumn("bedLocation", ColumnType.TEXT).setNull(true)
        ).create(MY_SQL_DATABASE);
        new TableConstructor("Kits",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
                new TableColumn("playerID", ColumnType.INT_11),
                new TableColumn("kit", ColumnType.VARCHAR_48),
                new TableColumn("date", ColumnType.TIMESTAMP).setNull(false)
        ).create(MY_SQL_DATABASE);
        new TableConstructor("Homes",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
                new TableColumn("playerID", ColumnType.INT_11),
                new TableColumn("name", ColumnType.VARCHAR_16),
                new TableColumn("location", ColumnType.TEXT).setNull(true)
        ).create(MY_SQL_DATABASE);
        new TableConstructor("Warps",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
                new TableColumn("name", ColumnType.VARCHAR_32),
                new TableColumn("playerID", ColumnType.INT_11),
                new TableColumn("location", ColumnType.TEXT).setNull(true),
                new TableColumn("private", ColumnType.BOOLEAN).setDefaultValue("0"),
                new TableColumn("date", ColumnType.TIMESTAMP).setNull(false)
        ).create(MY_SQL_DATABASE);
    }

    public MySqlDatabase getMySqlDatabase() {
        return MY_SQL_DATABASE;
    }
}