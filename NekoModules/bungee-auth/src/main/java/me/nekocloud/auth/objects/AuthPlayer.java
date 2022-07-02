package me.nekocloud.auth.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.auth.manager.AuthManager;
import me.nekocloud.auth.sql.AuthSql;
import me.nekocloud.auth.sql.QueryType;
import me.nekocloud.auth.utils.RedirectUtil;
import org.jetbrains.annotations.Nullable;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.sql.Timestamp;

import static lombok.AccessLevel.PRIVATE;
import static me.nekocloud.auth.sql.QueryType.UPD_LAST_ADDRESS_QUERY;
import static me.nekocloud.auth.sql.QueryType.UPD_SESSION_TIME_QUERY;

@Getter @Setter
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AuthPlayer {

    final String playerName;
    final int playerID;

    String playerPassword;

    long vkId = -1;
    long discordId = -1;
    int codeTypeId = 0;

    Timestamp registerDate;
    Timestamp expireSessionTime;

    String registerIp;
    String lastIp;

    public void initialize() {
        AuthSql.getDatabase().executeQuery(QueryType.INJECT_PLAYER_QUERY.get(), resultSet -> {
            if (!resultSet.next())
                return null;

            // Инициализация всех данных игрока
            setVkId(resultSet.getLong("VkId"));
            setDiscordId(resultSet.getLong("DiscordId"));
            setCodeTypeId(resultSet.getInt("CodeType"));

            setPlayerPassword(resultSet.getString("Password"));

            setRegisterDate(resultSet.getTimestamp("RegisterDate"));
            setExpireSessionTime(resultSet.getTimestamp("ExpireSessionTime"));

            setRegisterIp(resultSet.getString("RegisterAddress"));
            setLastIp(resultSet.getString("LastAddress"));

            return null;
        }, playerID);
    }

    /**
     * Установить новый пароль
     * Если null то игрока удалит из бд
     * @param newPlayerPassword пароль
     */
    public void setNewPassword(final @Nullable String newPlayerPassword) {
        if (newPlayerPassword == null) {
            AuthSql.getDatabase().execute(QueryType.DEL_PLAYER_QUERY.get(), playerID);
            logout();
            return;
        }

        playerPassword = AuthManager.INSTANCE.hashPassword(newPlayerPassword);
        AuthSql.getDatabase().execute(QueryType.UPD_PASSWORD_QUERY.get(), playerPassword, playerID);
    }

    /**
     * Завершить сессию
     */
    public void logout() {
        val gamer = getHandle();
        if (gamer != null) {
            val lang = gamer.getLanguage();
            AuthManager.INSTANCE.removeSession(this);
            gamer.disconnect(lang.getMessage("AUTH_LOGOUT"));
        }

        expireSessionTime = null;
        AuthSql.getDatabase().execute(QueryType.UPD_SESSION_TIME_QUERY.get(), expireSessionTime, playerID);
    }














    public boolean hasVKUser() {
        return (vkId != -1);
    }
    public boolean hasDiscordUser() {
        return (discordId != -1);
    }

    /**
     * Авториризрован ли игрок
     */
    public boolean isAuthorized() {
        return lastIp != null &&
                    lastIp.equals(getHandle().getIp().getHostAddress()) &&
                    expireSessionTime != null &&
                    expireSessionTime.getTime() - System.currentTimeMillis() > 0;
    }

    /**
     * Обновить последний ип
     */
    public void updateLastAddress() {
        if (lastIp == null) return;
        lastIp = getHandle().getIp().getHostAddress();

        AuthSql.getDatabase().execute(
                UPD_LAST_ADDRESS_QUERY.get(),
                lastIp,
                playerID
        );
    }

    /**
     * Обновить сессию
     */
    public void updateSessionTime() {
        expireSessionTime = new Timestamp(System.currentTimeMillis() + AuthManager.EXPIRE_SESSION_MILLIS);

        AuthSql.getDatabase().execute(
                UPD_SESSION_TIME_QUERY.get(),
                expireSessionTime,
                playerID
        );
    }

    /**
     * Получить геймера
     * @return объект геймера
     */
    public BungeeGamer getHandle() {
        return BungeeGamer.getGamer(playerID);
    }

    /**
     * Завершить процесс авторизации,
     * сохранить сесиию и перенаправить
     * игрока на лучший хаб сервер
     *
     * вызывается только при регистрации.
     * В остальных случаях надо отправлять пакет
     * BungeeAuthData на кору с акшионом WAIT_COMPLETE
     */
    public void completeDefault() {
        updateSessionTime();
        updateLastAddress();

        getHandle().getPlayer().connect(RedirectUtil.getBestHub());
    }


}
