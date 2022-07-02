package me.nekocloud.lobby.config;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.lobby.game.top.LobbyTop;
import me.nekocloud.lobby.game.top.StatsType;
import me.nekocloud.lobby.game.top.TopTable;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TopConfig extends LobbyConfig {

    private final List<Location> standLocations = new ArrayList<>();
    private final List<TopTable> topTables = new ArrayList<>();

    private int time = 0;
    private String database;

    public TopConfig(Lobby lobby) {
        super(lobby, "top");
    }

    @Override
    public void load() {
        val config = getConfig();

        config.getStringList("Locations").forEach(stringLoc -> {
            Location location = LocationUtil.stringToLocation(stringLoc, true);
            standLocations.add(location);
        });

        database = config.getString("Main");
        time = config.getInt("Time");

        for (val table : config.getConfigurationSection("Tables").getKeys(false)) {
            val path = "Tables." + table + ".";
            val tableName = config.getString(path + "Table");
            val holoName = config.getString(path + "NameHolo");

            val locale = config.getString(path + "Key");
            val commonWords = CommonWords.valueOf(locale);

            topTables.add(new TopTable(tableName, holoName, commonWords, StatsType.ALL, holoName, locale));
            topTables.add(new TopTable(tableName, holoName, commonWords, StatsType.MONTHLY, holoName, locale));
        }
    }

    @Override
    public void init() {
        new LobbyTop(this, database);
    }
}
