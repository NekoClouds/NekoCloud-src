package me.nekocloud.core.discord.user;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.discord.api.handler.SessionMessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@AllArgsConstructor
public class DiscordUser {

    /**
     * Пустой юзер, используется для тех кто еще не зареган, чтобы не возвращать NULL
     */
    public static final DiscordUser EMPTY_DS_BOT_USER = new DiscordUser(-1, null);

    /**
     * Получить ид дс
     */
    private @Getter final long discordID;

    /**
     * Получить акк привязанный к дс
     */
    private @Getter String primaryAccountName;

    /**
     * Добавить новый привязанный аккаунт к Discord
     * @param playerName ник игрока
     */
    public void addLinkedAccount(@NotNull String playerName) {
        CoreSql.getDatabase().execute("UPDATE `premium_auth` SET `DiscordId`=? WHERE `Id`=?",
                discordID, NetworkManager.INSTANCE.getPlayerID(playerName));

        this.primaryAccountName = playerName;
    }

    /**
     * Удалить привязанный к Discord аккаунт
     */
    public void removeLinkedAccount() {
        CoreSql.getDatabase().execute("UPDATE `premium_auth` SET `DiscordId`=? WHERE `Id`=?",
                -1, NetworkManager.INSTANCE.getPlayerID(primaryAccountName));

        this.primaryAccountName = null;
    }

    /**
     * Проверить, привязан ли к данному пользователю VK данный аккаунт
     *
     * @param playerName - ник игрока
     * @return - true, если аккаунт привязан
     * false, если нет
     */
    public boolean hasLinkedAccount(@NotNull String playerName) {
        return hasPrimaryAccount() && primaryAccountName.equalsIgnoreCase(playerName);
    }

    /**
     * Проверить, установлен ли у игрока основной аккаунт
     *
     * @return - установлен ли у игрока основной аккаунт
     */
    public boolean hasPrimaryAccount() {
        return primaryAccountName != null;
    }

    private final Map<String, SessionMessageHandler> sessionMessageHandlerMap = new ConcurrentHashMap<>();

    public boolean hasMessageHandlers() {
        return !sessionMessageHandlerMap.isEmpty();
    }

    public void handleMessageHandlers(@NotNull String messageBody) {
        for (SessionMessageHandler sessionMessageHandler : sessionMessageHandlerMap.values()) {
            sessionMessageHandler.onMessage(messageBody);
        }
    }

    public void createSessionMessageHandler(@NotNull String handlerTempName, @NotNull SessionMessageHandler sessionMessageHandler) {
        sessionMessageHandlerMap.put(handlerTempName.toLowerCase(), sessionMessageHandler);
    }

    public void closeSessionMessageHandler(@NotNull String handlerTempName) {
        sessionMessageHandlerMap.remove(handlerTempName.toLowerCase());
    }

    private static final TLongObjectMap<DiscordUser> discordUserMap = new TLongObjectHashMap<>();

    public static DiscordUser getDiscordUser(String playerName, long discordID) {
        DiscordUser discordUser = discordUserMap.get(discordID);

        if (discordUser == null)
            discordUserMap.put(discordID, (discordUser = new DiscordUser(discordID,null)));

        if (playerName != null && !discordUser.hasPrimaryAccount()) {
            discordUser.addLinkedAccount(playerName);
        }

        return discordUser;
    }

    public static DiscordUser getDiscordUser(long discordID) {
        if (discordUserMap.containsKey(discordID))
            return discordUserMap.get(discordID);

        val playerName = CoreSql.getDatabase().executeQuery("SELECT * FROM `premium_auth` WHERE `DiscordId`=?", resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            return NetworkManager.INSTANCE.getPlayerName(resultSet.getInt("Id"));
        }, discordID);

        return getDiscordUser(playerName, discordID);
    }
}
