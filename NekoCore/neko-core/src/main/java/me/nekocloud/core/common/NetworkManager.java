package me.nekocloud.core.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.api.connection.INetworkManager;
import me.nekocloud.core.common.group.GroupManager;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class NetworkManager implements INetworkManager {

    public static NetworkManager INSTANCE = new NetworkManager();

    BiMap<Integer, String> playerIdsMap = HashBiMap.create();

    static String GET_PLAYER_ID_QUERY = "SELECT `id` FROM `identifier` WHERE `player_name` = ? LIMIT 1";
    static String GET_PLAYER_NAME_QUERY = "SELECT `player_name` FROM `identifier` WHERE `id` = ? LIMIT 1";

    Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");


    /**
     * Получить ник игрока по его номеру
     * @param playerID - номер игрока
     */
    @Override
    public String getPlayerName(int playerID) {
        if (playerID == -1) {
            return "";
        }

        String playerName = playerIdsMap.get(playerID);

        if (playerName == null) {
            playerName = CoreSql.getDatabase().executeQuery(GET_PLAYER_NAME_QUERY,
                (rs) -> rs.next() ? rs.getString("player_name") : "", playerID);

            if (playerName != null) {
                playerIdsMap.put(playerID, playerName);
            }
        }

        return playerName;
    }

    /**
     * Получить номер игрока по его нику
     * @param playerName - ник игрока
     */
    @Override
    public int getPlayerID(String playerName) {
        if (!NAME_PATTERN.matcher(playerName).matches() || playerName.length() < 3) {
            return -1;
        }

        int playerId = playerIdsMap.inverse().getOrDefault(playerName, -1);

        try {
            if (playerId < 0) {
                playerId = CoreSql.getDatabase().executeQuery(GET_PLAYER_ID_QUERY,
                (rs) -> rs.next() ? rs.getInt("id") : -1, playerName);

                if (playerId >= 0) {
                    playerIdsMap.put(playerId, playerName);
                }
            }
        } catch (Exception ex) {
            return -1;
        }

        return playerId;
    }

    @Override
    public boolean hasIdentifier(String name) {
        return getPlayerID(name) != -1;
    }

    /**
     * Получить группу игрока по номеру игрока
     *
     * @param playerId - номер игрока
     */
    @Override
    public Group getPlayerGroup(int playerId) {
        return GroupManager.INSTANCE.getPlayerGroup(playerId);
    }

    /**
     * Получить группу игрока по нику самого игрока
     *
     * @param playerName - ник игрока
     */
    @Override
    public Group getPlayerGroup(final @NotNull String playerName) {
        int playerId = getPlayerID(playerName);

        return getPlayerGroup(playerId);
    }

}
