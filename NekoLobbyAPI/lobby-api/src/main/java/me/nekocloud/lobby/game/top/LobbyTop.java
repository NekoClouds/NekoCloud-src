package me.nekocloud.lobby.game.top;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.lobby.config.TopConfig;
import me.nekocloud.nekoapi.tops.armorstand.TopManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
public class LobbyTop {

    private final TopManager topManager;
    private final TopSql topSql;

    private final List<StatsTop> data = new ArrayList<>();

    public LobbyTop(TopConfig config, String table) {
        List<Location> standLocations = config.getStandLocations();
        topManager = new TopManager(config.getLobby(), table);
        topSql = new TopSql(standLocations.size());

        val center = LocationUtil.getCenter(standLocations);
        val mainHoloLocation = LocationUtil.getDirection(center.clone(), 1.0D)
                .subtract(0.0D, 0.55D, 0.0D);

        for (val topTable : config.getTopTables()) {
            val statsTopType = new StatsTop(config.getTime(), topTable, topManager, mainHoloLocation);
            topManager.createTop(statsTopType, standLocations);
            data.add(statsTopType);
        }

        val exec_service = Executors.newSingleThreadScheduledExecutor();
        exec_service.scheduleAtFixedRate(this::update, 0L, config.getTime(), TimeUnit.MINUTES);

    }

    private void update() {
        topSql.update(topManager, data);
    }
}
