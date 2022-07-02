package me.nekocloud.core.connection.player.offline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.player.IOfflineData;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.connection.server.BukkitServer;

@Getter
@Setter
@RequiredArgsConstructor // TODO перенести сюда наигранное время еще
public class OfflineData implements IOfflineData { // TODO переписать этот ебаный говнокод

    private final CorePlayer corePlayer;
    private String lastServerName = "auth-1"; // пусть по дефлоту будет эта параша

    @Override
    public Bukkit getLastServer() {
        val bukkit = NekoCore.getInstance().getBukkit(getLastServerName());
        if (bukkit == null) return new BukkitServer("auth-1", 1337);

        return bukkit;
    }

    @Override
    public long getLastOnline() {
        return corePlayer.getLastOnline();
    }

    @Override
    public String getLastServerName() {
        this.lastServerName = CoreSql.getDatabase().executeQuery(
                "SELECT `server` FROM `join_info` WHERE `id` = ? LIMIT 1;", (rs) -> {
            if (rs.next()) return (lastServerName = rs.getString("server"));

            return null;
        }, corePlayer.getPlayerID());

        return lastServerName;
    }

}
