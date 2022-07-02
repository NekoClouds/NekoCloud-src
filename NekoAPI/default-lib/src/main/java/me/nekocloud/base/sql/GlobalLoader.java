package me.nekocloud.base.sql;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.StatementWrapper;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import me.nekocloud.base.sql.api.query.QuerySymbol;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.base.util.Pair;
import me.nekocloud.base.util.RandomUtil;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@UtilityClass
public class GlobalLoader {

    private final MySqlDatabase MYSQL_DATABASE = MySqlDatabase.newBuilder()
            .data("core")
            .host(ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .user("neko")
            .create();

    private final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    public MySqlDatabase getMysqlDatabase() {
        return MYSQL_DATABASE;
    }

    public final int LICENSE_VERIFYING = 0;
    public final int LICENSE_VERIFIED = 1;

    private final String GET_FRIENDS_ID_QUERY = "SELECT * FROM `friends` WHERE `id` = ?";
    //==//
    private final String GET_SKIN_VALUE_QUERY = "SELECT `value` FROM `skins_storage` WHERE `skin` = ? LIMIT 1;";
    private final String GET_SKIN_INFO_QUERY = "SELECT * FROM `skins_storage` WHERE `skin` = ? LIMIT 1;";
    //==//
    private final String GET_SKIN_QUERY = "SELECT `skin` FROM `selected_skins` WHERE `id` = ? LIMIT 1;";
    private final String UPD_SKIN_QUERY = "UPDATE `selected_skins` SET `skin` = ? WHERE `id` = ?;";
    private final String INS_SKIN_QUERY = "INSERT INTO `selected_skins` (`skin`, `id`) VALUES (?, ?);";
    //==//
    private final String GET_PLAYER_ID_QUERY = "SELECT `id` FROM `identifier` WHERE `player_name` = ? LIMIT 1";
    private final String GET_PLAYER_INFO = "SELECT * FROM `identifier` WHERE `player_name` = ? LIMIT 1";
    private final String GET_PLAYER_INFO_BY_ID = "SELECT * FROM `identifier` WHERE `id` = ? LIMIT 1";
    private final String GET_PLAYER_NAME_QUERY = "SELECT `player_name` FROM `identifier` WHERE `id` = ? LIMIT 1";
    //==//
    private final String SELECT_LANGUAGE = "SELECT `language` FROM `identifier` WHERE `id` = ?";
    private final String UPDATE_LANGUAGE = "UPDATE `identifier` SET `language` = ? WHERE `id` = ?";
    //==//
    private final String INSERT_PLAYER_ID = "INSERT INTO `identifier` (`player_name`) VALUES (?)";
    private final String GET_GROUP_QUERY = "SELECT `group_id` FROM `players_groups` WHERE `id` = ? LIMIT 1;";
    private final String SET_GROUP_QUERY = "INSERT INTO `players_groups` (`id`, `group_id`) VALUES (?, ?)";
    private final String DEL_GROUP_QUERY = "DELETE FROM `players_groups` WHERE `id` = ?";
    //==//
    private final String GET_SETTINGS_QUERY = "SELECT * FROM `settings` WHERE `id` = ?;";
    //==//
    private final String GET_PREFIX_QUERY = "SELECT `prefix` FROM `custom_prefixes` WHERE `id` = ? LIMIT 1;";
    private final String UPD_PREFIX_QUERY = "UPDATE `custom_prefixes` SET `prefix` = ? WHERE `id` = ?;";
    private final String INS_PREFIX_QUERY = "INSERT INTO `custom_prefixes` (`id`, `prefix`) VALUES (?, ?);";
    //==//
    private final String GET_LICENSE_QUERY = "SELECT * FROM `premium_auth` WHERE `Id` = ? LIMIT 1;";
    private final String UPD_LICENSE_QUERY = "UPDATE `Id` SET `License` = ? WHERE `player_name` = ?";
    private final String INSERT_LICENSE_QUERY = "INSERT INTO `Id` (`player_name`, `License`) VALUES (?, ?)";
    //==//
    private final String GET_TEMP_PREFIX_QUERY = "SELECT * FROM `temp_prefixes` WHERE `id` = ? LIMIT 1";
    private final String DELETE_TEMP_PREFIX_QUERY = "DELETE FROM `temp_prefixes` WHERE `id` = ?";
    private final String SET_TEMP_PREFIX_QUERY = "INSERT INTO `temp_prefixes` (id, prefix, expires) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `prefix` = ?, `expires` = ?";

    // Так надо :(
    public boolean loadLicense(String name) {
        if (!NAME_PATTERN.matcher(name).matches() || name.length() < 3) {
            return false;
        }

        int playerID = getPlayerID(name);
        return MYSQL_DATABASE.executeQuery(GET_LICENSE_QUERY,
                (rs) -> rs.next() && rs.getBoolean("License"), playerID);
    }

    public void setLicense(String name, boolean result) {
        if (!NAME_PATTERN.matcher(name).matches() || name.length() < 3) {
            return;
        }

        int playerID = getPlayerID(name);
        MYSQL_DATABASE.executeQuery(GET_LICENSE_QUERY, (rs) -> {
            if (rs.next()) {
                MYSQL_DATABASE.execute(UPD_LICENSE_QUERY, result, name);
            } else {
                MYSQL_DATABASE.execute(INSERT_LICENSE_QUERY, name, result);
            }

            return Void.TYPE;
        }, playerID);
    }

    public String getTempPrefix(int playerID) {
        return MYSQL_DATABASE.executeQuery(GET_TEMP_PREFIX_QUERY, rs -> {
            if (rs.next()) {
                Timestamp date = rs.getTimestamp("expires");
                if (date.getTime() < System.currentTimeMillis()) {
                    MYSQL_DATABASE.execute(DELETE_TEMP_PREFIX_QUERY, playerID);
                } else {
                    return rs.getString("prefix");
                }
            }

            return null;
        }, playerID);
    }

    public void setTempPrefix(int playerID, String prefix, long time) {
        Timestamp timestamp = new Timestamp(time);
        MYSQL_DATABASE.execute(SET_TEMP_PREFIX_QUERY, playerID, prefix, timestamp, prefix, timestamp);
    }

    public void setMoney(int playerID, PurchaseType purchaseType, int value, boolean insert) {
        if (playerID == -1) {
            return;
        }

        if (insert) {
            MYSQL_DATABASE.execute(MysqlQuery.insertTo("players_money")
                    .set("playerId", playerID)
                    .set("typeMoney", purchaseType.getId())
                    .set("value", value)
            );
            return;
        }

        MYSQL_DATABASE.execute(MysqlQuery.update("players_money")
                .set("value", value)
                .where("typeMoney", QuerySymbol.EQUALLY, purchaseType.getId())
                .where("value", QuerySymbol.EQUALLY, value));

    }

    public void changeMoney(int playerID, PurchaseType purchaseType, int value, boolean insert) {
        if (playerID == -1) {
            return;
        }

        if (insert) {
            MYSQL_DATABASE.execute(MysqlQuery.insertTo("players_money")
                    .set("playerId", playerID)
                    .set("typeMoney", purchaseType.getId())
                    .set("value", value)
            );
            return;
        }

        MYSQL_DATABASE.execute("UPDATE `players_money` SET `value`=`value` + '"
                + value + "' WHERE `playerId`=" + playerID + " AND `typeMoney` = " + purchaseType.getId() + ";");
    }

    /**
     * language - язык(boolean - в бд иди выбранный)
     * map - настройки
     */
    public Pair<Pair<Language, Boolean>, Object2BooleanMap<SettingsType>> getSettings(int playerID) {
        if (playerID == -1) {
            return new Pair<>(new Pair<>(Language.DEFAULT, false), new Object2BooleanOpenHashMap<>());
        }

        return MYSQL_DATABASE.executeQuery(GET_SETTINGS_QUERY, (rs) -> {
            Language language = Language.DEFAULT;
            boolean languageMysql = false;

            Object2BooleanMap<SettingsType> settings = new Object2BooleanOpenHashMap<>();

            while (rs.next()) {
                int id = rs.getInt("setting_id");
                int value = rs.getInt("setting_value");

                if (id == SettingsType.LANGUAGE_KEY) { //айди настройки для языка
                    language = Language.getLanguage(value);
                    languageMysql = true;
                    continue;
                }

                SettingsType settingsType = SettingsType.getSettingType(id);
                if (settingsType != null) {
                    settings.put(settingsType, value == 1);
                }
            }

            return new Pair<>(new Pair<>(language, languageMysql), settings);
        }, playerID);
    }

    public Group getGroup(int playerID) {
        if (playerID == -1) {
            return Group.DEFAULT;
        }

        return MYSQL_DATABASE.executeQuery(GET_GROUP_QUERY,
                (rs) -> rs.next() ? Group.getGroup(rs.getInt("group_id")) : Group.DEFAULT, playerID);
    }

    public void setGroup(int playerID, int groupID) {
        if (playerID == -1) {
            return;
        }

        MYSQL_DATABASE.execute(DEL_GROUP_QUERY, playerID);

        if (groupID != 0) {
            MYSQL_DATABASE.execute(SET_GROUP_QUERY, playerID, groupID);
        }
    }


    public Pair<String, Integer> getInfo(String name) {
        return MYSQL_DATABASE.executeQuery(GET_PLAYER_INFO, (rs) -> {
            Pair<String, Integer> pair = new Pair<>(name, -1);

            if (rs.next()) {
                pair = new Pair<>(rs.getString("player_name"), rs.getInt("id"));
            }

            return pair;
        }, name);
    }

    public void setPlayerName(int id, String name) {
        MYSQL_DATABASE.execute("UPDATE `identifier` SET `player_name` = ? WHERE `id` = ?", name, id);
    }

    public void changePlayersStats(int playerID1, int playerID2) {
        String name1 = getName(playerID2);
        String name2 = getName(playerID1);

        if (name1.equalsIgnoreCase("") || name2.equalsIgnoreCase("")) {
            return;
        }

        MYSQL_DATABASE.execute(MysqlQuery.deleteFrom("identifier")
                .where("id", QuerySymbol.EQUALLY, playerID1)
                .limit());
        MYSQL_DATABASE.execute(MysqlQuery.deleteFrom("identifier")
                .where("id", QuerySymbol.EQUALLY, playerID2)
                .limit());
        MYSQL_DATABASE.execute(MysqlQuery.insertTo("identifier")
                .set("id", playerID1)
                .set("player_name", name1));
        MYSQL_DATABASE.execute(MysqlQuery.insertTo("identifier")
                .set("id", playerID2)
                .set("player_name", name2));
    }

    public String getDisplayName(int playerID) {
        if (playerID == -1) {
            return "§cError!";
        }

        return MYSQL_DATABASE.executeQuery(GET_PLAYER_NAME_QUERY, (rs) -> {
            String displayName = "§cError!";

            if (rs.next()) {
                displayName = rs.getString("player_name");
            }

            return "§r" + getPrefix(playerID, getGroup(playerID)) + displayName;
        }, playerID);
    }

    public String getName(int playerID) {
        if (playerID == -1) {
            return "";
        }

        return MYSQL_DATABASE.executeQuery(GET_PLAYER_NAME_QUERY,
                (rs) -> rs.next() ? rs.getString("player_name") : "", playerID);
    }

    public IntList getFriends(int playerID) {
        IntArrayList friends = new IntArrayList();

        if (playerID == -1) {
            return friends;
        }

        return MYSQL_DATABASE.executeQuery(GET_FRIENDS_ID_QUERY, (rs) -> {
            while (rs.next()) {
                friends.add(rs.getInt("friend_id"));
            }

            return friends;
        }, playerID);
    }

    public int getPlayerID(String name) {
        if (!NAME_PATTERN.matcher(name).matches() || name.length() < 3) {
            return -1;
        }

        return MYSQL_DATABASE.executeQuery(GET_PLAYER_ID_QUERY, (rs) -> {
            int playerID;

            if (rs.next()) {
                playerID = rs.getInt(1);
            } else {
                playerID = StatementWrapper
                        .create(MYSQL_DATABASE, INSERT_PLAYER_ID)
                        .execute(PreparedStatement.RETURN_GENERATED_KEYS, name);
            }

            return playerID;
        }, name);
    }

    public int containsPlayerID(String name) {
        if (!NAME_PATTERN.matcher(name).matches() || name.length() < 3) {
            return -1;
        }

        return MYSQL_DATABASE.executeQuery(GET_PLAYER_ID_QUERY,
                (rs) -> rs.next() ? rs.getInt(1) : -1, name);
    }

    public void setPrefix(int playerID, String value) {
        if (playerID == -1) {
            return;
        }

        MYSQL_DATABASE.executeQuery(GET_PREFIX_QUERY, (rs) -> {
            if (rs.next()) {
                MYSQL_DATABASE.execute(UPD_PREFIX_QUERY, value, playerID);
            } else {
                MYSQL_DATABASE.execute(INS_PREFIX_QUERY, playerID, value);
            }

            return Void.TYPE;
        }, playerID);
    }

    public String getPrefix(int playerID, Group group) {
        if (playerID == -1) {
            return Group.DEFAULT.getPrefix();
        }

        String prefix = GlobalLoader.getTempPrefix(playerID);
        if (prefix == null) {
            prefix = group.getPrefix();
        }

        if ((group != Group.NEKO
                && group != Group.ADMIN
                && group != Group.OWNER
                && group != Group.DEVELOPER) || prefix.toLowerCase().contains("test")) {
            return prefix;
        }

        String charPrefix = getPrefixChar(playerID);
        if (charPrefix.length() > 1) {
            prefix = prefix.replaceAll("§[0-9a-e]", charPrefix.replaceAll("&", "§"));
        }

        return prefix;
    }

    public String getPrefixChar(int playerID) {
        if (playerID == -1) {
            return "";
        }

        return MYSQL_DATABASE.executeQuery(GET_PREFIX_QUERY,
                (rs) -> rs.next() ? rs.getString("prefix") : "", playerID);
    }

    public void setSetting(int playerID, int type, int value, boolean insert) {
        if (playerID == -1) {
            return;
        }

        if (insert) {
            MYSQL_DATABASE.execute(MysqlQuery.insertTo("settings")
                    .set("id", playerID)
                    .set("setting_id", type)
                    .set("setting_value", value)
            );
            return;
        }

        MYSQL_DATABASE.execute(MysqlQuery.update("settings")
                .where("id", QuerySymbol.EQUALLY, playerID)
                .where("setting_id", QuerySymbol.EQUALLY, type)
                .set("setting_value", value));
    }

    public Map<PurchaseType, Integer> getPlayerMoney(int playerID) {
        if (playerID == -1) {
            return new HashMap<>();
        }

        return MYSQL_DATABASE.executeQuery(MysqlQuery.selectFrom("players_money")
                .where("playerId", QuerySymbol.EQUALLY, playerID)
                .limit(PurchaseType.values().length), (rs) -> {
            Map<PurchaseType, Integer> moneys = new HashMap<>();

            while (rs.next()) {
                PurchaseType purchaseType = PurchaseType.getType(rs.getInt("typeMoney"));
                if (purchaseType != null) {
                    moneys.put(purchaseType, rs.getInt("value"));
                }
            }

            return moneys;
        });
    }


    public void setSkinName(int playerID, String skinName) {
        if (playerID == -1) {
            return;
        }

        MYSQL_DATABASE.executeQuery(GET_SKIN_QUERY, (rs) -> {
            if (rs.next()) {
                MYSQL_DATABASE.execute(UPD_SKIN_QUERY, skinName, playerID);
            } else {
                MYSQL_DATABASE.execute(INS_SKIN_QUERY, skinName, playerID);
            }

            return Void.TYPE;
        }, playerID);
    }

    public String getSkinName(String name, int playerID) {
        if (playerID == -1) {
            return "_Novit_";
        }

        String select = MYSQL_DATABASE.executeQuery(GET_SKIN_QUERY,
                (rs) -> rs.next() ? rs.getString("skin") : null, playerID);

        if (select == null || select.length() < 3) {
            return name;
        }

        return select;
    }

    public String getSelectedSkin(String name, int playerID) {
        if (playerID == -1) {
            return "";
        }

        return getSkinValue(getSkinName(name, playerID));
    }

    public String getSkinValue(String skinName) {
        return MYSQL_DATABASE.executeQuery(GET_SKIN_VALUE_QUERY,
                (rs) -> rs.next() ? rs.getString(1) : null, skinName);
    }

    public Skin getSkin(String skinName) {
        return MYSQL_DATABASE.executeQuery(GET_SKIN_INFO_QUERY, (rs) -> {
            if (rs.next()) {
                return new Skin(skinName,
                        rs.getString("value"),
                        rs.getString("signature"),
                        SkinType.getSkinType(rs.getInt("skin_type")),
                        rs.getTimestamp("update_time").getTime());
            }

            int i = RandomUtil.getInt(1, 3);
            return switch (i) {
                case 2 -> Skin.DEFAULT_SKIN_TWO;
                case 3 -> Skin.DEFAULT_SKIN_THREE;
                default -> Skin.DEFAULT_SKIN;
            };
        }, skinName);
    }

    public Language getLanguage(int playerId) {
        if (playerId == -1) {
            return Language.DEFAULT;
        }

        return MYSQL_DATABASE.executeQuery(MysqlQuery.selectFrom("settings")
                .result("setting_value")
                .where("id", QuerySymbol.EQUALLY, playerId)
                .where("setting_id", QuerySymbol.EQUALLY, SettingsType.LANGUAGE_KEY).limit(), (rs) -> {
            if (rs.next()) {
                return Language.getLanguage(rs.getInt("setting_value"));
            } else {
                return Language.DEFAULT;
            }
        });
    }

    static {
        new TableConstructor("skins_storage",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true).autoIncrement(true),
                new TableColumn("skin", ColumnType.VARCHAR_16).primaryKey(true).unigue(true),
                new TableColumn("skin_type", ColumnType.INT_2).setDefaultValue(SkinType.CUSTOM.getTypeId()),
                new TableColumn("update_time", ColumnType.TIMESTAMP),
                new TableColumn("value", ColumnType.TEXT),
                new TableColumn("signature", ColumnType.TEXT)
        ).create(GlobalLoader.getMysqlDatabase());
    }

}

