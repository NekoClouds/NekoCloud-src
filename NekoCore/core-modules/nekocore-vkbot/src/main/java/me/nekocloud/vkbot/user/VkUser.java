package me.nekocloud.vkbot.user;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.vkbot.api.handler.SessionMessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class VkUser {

    /**
     * Пустой юзер, используется для тех кто еще не зареган, чтобы не возвращать NULL
     */
    public static final VkUser EMPTY_BOT_USER = new VkUser(-1, null);

    @Getter
    private final int vkId;

    /**
     * Имя основного аккаунта, используемоего на сервере
     */
    @Getter
    private String primaryAccountName;

    /**
     * Добавить новый привязанный аккаунт к VK
     *
     * @param playerName - ник игрока
     */
    public void addLinkedAccount(@NotNull String playerName) {
        CoreSql.getDatabase().execute("UPDATE `premium_auth` SET `VkId`=? WHERE `Id`=?",
                vkId, NetworkManager.INSTANCE.getPlayerID(playerName));

        this.primaryAccountName = playerName;
    }

    /**
     * Удалить привязанный к VK аккаунт
     */
    public void removeLinkedAccount() {
        CoreSql.getDatabase().execute("UPDATE `premium_auth` SET `VkId`=? WHERE `Id`=?",
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

    public static final Int2ObjectMap<VkUser> VK_USER_MAP = new Int2ObjectOpenHashMap<>();

    private static VkUser getVkUser(String playerName, int vkId) {
        VkUser vkUser = VK_USER_MAP.get(vkId);

        if (vkUser == null) {
            VK_USER_MAP.put(vkId, (vkUser = new VkUser(vkId, null)));
        }

        if (playerName != null && !vkUser.hasPrimaryAccount()) {
            vkUser.addLinkedAccount(playerName);
        }

        return vkUser;
    }

    public static VkUser getVkUser(int vkId) {
        if (VK_USER_MAP.containsKey(vkId))
            return VK_USER_MAP.get(vkId);

        String playerName = CoreSql.getDatabase().executeQuery("SELECT * FROM `premium_auth` WHERE `VkId`=?", resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            return NetworkManager.INSTANCE.getPlayerName(resultSet.getInt("Id"));
        }, vkId);

        return getVkUser(playerName, vkId);
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

}

