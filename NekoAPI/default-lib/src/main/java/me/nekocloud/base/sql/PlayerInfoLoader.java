package me.nekocloud.base.sql;

import lombok.experimental.UtilityClass;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import me.nekocloud.base.sql.api.query.QuerySymbol;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.base.util.Pair;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PlayerInfoLoader {

    private final MySqlDatabase MYSQL_DATABASE = MySqlDatabase.newBuilder()
            .data("lobby")
            .host(ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .user("neko")
            .create();

    public final int EXP_KEY = 0;

    static {
        new TableConstructor("networkingData",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true).autoIncrement(true),
                new TableColumn("playerId", ColumnType.INT_11).primaryKey(true),
                new TableColumn("typeData", ColumnType.INT_5).primaryKey(true),
                new TableColumn("value", ColumnType.INT_11).setDefaultValue(0),
                new TableColumn("pseudo", ColumnType.INT_5).setDefaultValue(0)
        ).create(MYSQL_DATABASE); // + добавить индексы
    }

    /**
     * integer - его опыт
     * с ключами все и так понятно
     * @param playerID - ник игрока
     * @return - получить данные
     * 1я пара (опыт | за какой последний лвл выдана награда)
     * 2я пара(которая в мапе) (кол-во ключей | псевдорандом)
     */
    public Pair<Pair<Integer, Integer>, Map<KeyType, Pair<Integer, Integer>>> getData(int playerID) {
        return MYSQL_DATABASE.executeQuery(MysqlQuery.selectFrom("networkingData")
                .where("playerId", QuerySymbol.EQUALLY, playerID), (rs) -> {

            Map<KeyType, Pair<Integer, Integer>> keys = new HashMap<>();
            int exp = 0;
            int giveRewardLevel = 0;

            while (rs.next()) {
                int typeData = rs.getInt("typeData");
                int value = rs.getInt("value");
                int pseudo = rs.getInt("pseudo");
                if (typeData == EXP_KEY) {
                    exp = value;
                    giveRewardLevel = pseudo;
                } else {
                    KeyType keyType = KeyType.getKey(typeData);
                    if (keyType == null) {
                        continue;
                    }

                    keys.put(keyType, new Pair<>(value, pseudo));
                }
            }

            return new Pair<>(new Pair<>(exp, giveRewardLevel), keys);
        });
    }

    public void setData(int playerID, int typeData, int value, boolean insert) {
        if (playerID == -1) {
            return;
        }

        if (insert) {
            MYSQL_DATABASE.execute(MysqlQuery.insertTo("networkingData")
                    .set("value", value)
                    .set("playerId", playerID)
                    .set("typeData", typeData));

            return;
        }

        MYSQL_DATABASE.execute(MysqlQuery.update("networkingData")
                .where("playerId", QuerySymbol.EQUALLY, playerID)
                .where("typeData", QuerySymbol.EQUALLY, typeData)
                .set("value", value));
    }

    public void updateData(int playerID, int typeData, int value, int pseudo, boolean insert) {
        if (playerID == -1) {
            return;
        }

        if (insert) {
            MYSQL_DATABASE.execute(MysqlQuery.insertTo("networkingData")
                    .set("value", value)
                    .set("playerId", playerID)
                    .set("typeData", typeData)
                    .set("pseudo", pseudo));

            return;
        }

        MYSQL_DATABASE.execute("UPDATE `networkingData` SET `value`=`value` + '"
                + value + "', `pseudo` = '" + pseudo + "' WHERE `playerId`=" + playerID + " AND `typeData` = " + typeData + ";");
    }

    public MySqlDatabase getMysqlDatabase() {
        return MYSQL_DATABASE;
    }
}
