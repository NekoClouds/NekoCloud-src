package me.nekocloud.siteshop;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import me.nekocloud.base.sql.api.query.QuerySymbol;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.siteshop.item.PlayerSSItem;
import me.nekocloud.siteshop.item.SSItem;
import me.nekocloud.siteshop.item.SSItemManager;

import java.util.ArrayList;
import java.util.List;

public final class ItemsLoader {

    private final MySqlDatabase database;

    public ItemsLoader(String dataBase) {
        database = MySqlDatabase.newBuilder()
                .user("root")
                .host("s1" + ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .data(dataBase)
                .create();
        init();
    }

    private void init() {
        new TableConstructor("SiteShopItems",
                new TableColumn("id", ColumnType.INT_11).autoIncrement(true).primaryKey(true),
                new TableColumn("item_id", ColumnType.INT_11),
                new TableColumn("player_id", ColumnType.INT_11)
        ).create(database);
    }

    public List<PlayerSSItem> loadItem(BukkitGamer gamer, SSItemManager itemManager) {
        int playerId = gamer == null ? -1 : gamer.getPlayerID();
        if (playerId == -1) {
            return new ArrayList<>();
        }

        return database.executeQuery(MysqlQuery.selectFrom("SiteShopItems")
                .where("player_id", QuerySymbol.EQUALLY, playerId), (rs) -> {
            List<PlayerSSItem> items = new ArrayList<>();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                SSItem ssItem = itemManager.getItem(itemId);
                if (ssItem == null) {
                    continue;
                }

                items.add(new PlayerSSItem(ssItem));
            }

            return items;
        });
    }

    public void giveToPlayer(BukkitGamer gamer, SSItem ssItem) {
        if (gamer == null) {
            return;
        }

        database.execute(MysqlQuery.deleteFrom("SiteShopItems")
                .where("item_id", QuerySymbol.EQUALLY, ssItem.getId())
                .where("player_id", QuerySymbol.EQUALLY, gamer.getPlayerID())
                .limit());
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
