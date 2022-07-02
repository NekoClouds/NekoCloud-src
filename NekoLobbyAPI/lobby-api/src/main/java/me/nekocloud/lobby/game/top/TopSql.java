package me.nekocloud.lobby.game.top;

import lombok.SneakyThrows;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.nekoapi.tops.armorstand.StandTopData;
import me.nekocloud.nekoapi.tops.armorstand.TopManager;
import me.saharnooby.lib.query.query.Query;
import me.saharnooby.lib.query.set.ResultSetWrapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

class TopSql {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    private final MySqlDatabase database;

    private final int limit;

    TopSql(int limit) {
        database = MySqlDatabase.newBuilder()
                .data("GameStats")
                .host(ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .user("neko")
                .property("maximumPoolSize", "5")
                .property("minimumIdle", "1")
                .create();
        this.limit = limit;
    }

    @SneakyThrows
    void update(TopManager topManager, List<StatsTop> topStatsTypes) {
        try (Connection connection = database.getConnection()) {
            for (StatsTop statsTopType : topStatsTypes) {
                TopTable topTable = statsTopType.getTopTable();
                val nameTable = topTable.getTable();
                val type = topTable.getType().getValue();
                ResultSetWrapper rs = (Query.select().all()
                        .from(nameTable)
                        .where("type", type)
                        .orderBy(topTable.getColumn())
                        .desc().limit(limit + 1)
                        .query(connection));
                List<StandTopData> topStandData = new ArrayList<>();
                int pos = 1;

                while (rs.set().next()) {
                    val playerID = rs.set().getInt("userID");
                    val value = rs.set().getInt(topTable.getColumn());
                    val gamer = gamerManager.getOrCreate(playerID);

                    if (gamer != null)
                        topStandData.add(new LobbyTopData(statsTopType, gamer, value, pos));
                    pos++;
                }
                topManager.updateStandData(topStandData);
            }

        }
    }
}
