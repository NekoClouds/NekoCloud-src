package me.nekocloud.core.common.auth.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.player.AuthSendCodeEvent;
import me.nekocloud.core.api.event.player.AuthSetDiscordEvent;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.auth.data.AuthAction;
import me.nekocloud.core.common.auth.data.AuthData;
import me.nekocloud.core.common.auth.sql.CoreAuthSql;
import me.nekocloud.core.common.auth.sql.QueryType;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;

import static lombok.AccessLevel.PRIVATE;
import static me.nekocloud.core.api.event.player.AuthSendCodeEvent.CodeType;

@Getter @Setter @Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CoreAuthPlayer {

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
        CoreAuthSql.getDatabase().executeQuery(QueryType.INJECT_PLAYER_QUERY.get(), resultSet -> {
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



    public boolean hasVKUser() {
        return (vkId != -1);
    }
    public boolean hasDiscordUser() {
        return (discordId != -1);
    }

    /**
     * Установить новый пароль
     * Если null то игрока удалит из бд
     * @param newPlayerPassword пароль
     */
    public void setNewPassword(final @Nullable String newPlayerPassword) {
        if (newPlayerPassword == null) {
            CoreAuthSql.getDatabase().execute(QueryType.DEL_PLAYER_QUERY.get(), playerID);
            if (getHandle() != null)
                logout();
            return;
        }

        playerPassword = CoreAuth.getAuthManager().hashPassword(newPlayerPassword);
        CoreAuthSql.getDatabase().execute(QueryType.UPD_PASSWORD_QUERY.get(), playerPassword, playerID);
    }

    public void logout() {
        NekoCore.getInstance().getPlayer(playerID).getBungee().sendPacket(
                new AuthData(playerID, AuthAction.LOGOUT)
        );
    }

    /**
     * Установить новый тег дс игроку
     * @param discordId ид дс
     */
    public void setNewDiscord(long discordId) {
        this.discordId = discordId;

        CoreAuthSql.getDatabase().execute(QueryType.UPD_DISCORD_QUERY.get(), discordId, playerID);

        NekoCore.getInstance().callEvent(
                new AuthSetDiscordEvent(this, discordId)
        );
    }

    public void updateSessionTime() {
        expireSessionTime = new Timestamp(System.currentTimeMillis() + CoreAuthSql.EXPIRE_SESSION_MILLIS);

        CoreAuthSql.getDatabase().execute(QueryType.UPD_SESSION_TIME_QUERY.get(), expireSessionTime, playerID);
    }

    public CorePlayer getHandle() {
        return NekoCore.getInstance().getPlayer(playerName);
    }

    public CorePlayer getOfflineHandle() {
        return NekoCore.getInstance().getOfflinePlayer(playerName);
    }

    public String getDiscordTag() {
        return CoreAuthSql.getDatabase().executeQuery(QueryType.GET_DISCORD_TAG_QUERY.get(), resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            return resultSet.getString("DiscordTag");
        }, discordId);
    }

    public void setAuthType(int codeTypeId) {
        this.codeTypeId = codeTypeId;
        CoreAuthSql.getDatabase().execute(QueryType.UPD_CODE_TYPE_QUERY.get(), codeTypeId, playerID);
    }

    public Integer getAuthType() {
        return CoreAuthSql.getDatabase().executeQuery(QueryType.GET_CODE_TYPE_QUERY.get(), resultSet -> {
            if (!resultSet.next())
                return 0;

            return resultSet.getInt("CodeType");
        }, playerID);
    }

    public void completeWith2FACode() {
        if (hasVKUser() || hasDiscordUser()) {
            CodeType codeType;

            try { // тут может возникнуть ошибка поэтому тут try
                codeType = CodeType.getType(getAuthType());
            } catch (Exception e) {
                codeType = CodeType.VK;
            }

            if (codeType == CodeType.VK) {
                CoreAuth.getAuthManager().add2FASession(playerID);
                NekoCore.getInstance().callEvent(new AuthSendCodeEvent(playerID, CodeType.VK));
                return;
            } else if (codeType == CodeType.DISCORD) {
                CoreAuth.getAuthManager().add2FASession(playerID);
                NekoCore.getInstance().callEvent(new AuthSendCodeEvent(playerID, CodeType.DISCORD));
                return;
            }
        }

        completeWith2FADefault();
    }

    public void completeWith2FADefault() {
        NekoCore.getInstance()
                .getPlayer(playerID)
                .getBungee()
                .sendPacket(new AuthData(playerID, AuthAction.SUCCESS_COMPLETE_WITH_2FA));
    }

    public void completeDefault() {
        NekoCore.getInstance()
                .getPlayer(playerID)
                .getBungee()
                .sendPacket(new AuthData(playerID, AuthAction.SUCCESS_COMPLETE_DEFAULT));
    }

}
