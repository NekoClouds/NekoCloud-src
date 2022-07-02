package me.nekocloud.nekoapi.tops.armorstand;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import me.nekocloud.base.sql.api.query.QuerySymbol;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

import javax.annotation.Nullable;

public final class StandTopSql {

    private final TopManager standTopManager;
    private final String table;

    private final MySqlDatabase database;

    StandTopSql(TopManager standTopManager, String table) {
        this.standTopManager = standTopManager;
        this.table = table;

        this.database = MySqlDatabase.newBuilder()
                .data("SelectedTop")
                .host(ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .user("neko")
                .create();

        new TableConstructor(table,
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true).unigue(true),
                new TableColumn("playerID", ColumnType.INT_11).unigue(true).primaryKey(true),
                new TableColumn("type", ColumnType.INT_11)
        ).create(this.database);
    }

    @Nullable
    public Top getSelectedType(BukkitGamer gamer) {
        if (gamer == null) {
            return null;
        }

        int playerID = gamer.getPlayerID();
        return database.executeQuery(MysqlQuery.selectFrom(table)
                .where("playerID", QuerySymbol.EQUALLY, playerID)
                .limit(), (rs) -> {
            if (rs.next()) {
                return standTopManager.getTop(rs.getInt("type"));
            }
            return null;
        });
    }

    void changeSelectedType(StandPlayer standPlayer) {
        BukkitGamer gamer = standPlayer.getGamer();
        if (gamer == null) {
            return;
        }

        int playerID = gamer.getPlayerID();
        int topTypeID = standPlayer.getTopType().getId();
        if (standPlayer.isNewPlayer()) {
            database.execute(MysqlQuery.insertTo(table)
                    .set("playerID", playerID)
                    .set("type", topTypeID));
        } else {
            database.execute(MysqlQuery.update(table)
                    .where("playerID", QuerySymbol.EQUALLY, playerID)
                    .set("type", topTypeID));
        }
    }

    void onDisable() {
        this.database.close();
    }
}
