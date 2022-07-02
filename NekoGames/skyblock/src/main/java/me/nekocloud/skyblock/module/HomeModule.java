package me.nekocloud.skyblock.module;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.IslandModule;
import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.manager.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class HomeModule extends IslandModule {

    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final UserManager USER_MANAGER = CommonsSurvivalAPI.getUserManager();

    private static final List<Material> BAD_CONDITIONS = Arrays.asList(
        Material.LAVA, Material.WATER, Material.WOOD_BUTTON, Material.PORTAL, Material.STONE_BUTTON,
        Material.STATIONARY_LAVA, Material.STATIONARY_WATER, Material.TORCH, Material.WEB,
        Material.FIRE, Material.SIGN_POST, Material.BOAT, Material.TRIPWIRE, Material.CACTUS, Material.AIR
    );

    private Location home;

    public HomeModule(Island island) {
        super(island);
    }

    @Override
    public void load(ResultSet rs) throws SQLException {
        this.home = LocationUtil.stringToLocation(rs.getString("home"), true);
    }

    @Override
    public void delete() {
        final int id = island.getIslandID();
        if (id == -1) {
            return;
        }

        DATABASE.execute("DELETE FROM `IslandHome` WHERE `island` = ? LIMIT 1;", id);
    }

    public void teleport(Player player) {
        //TODO поиск лучшей локации для ТП (чтобы без воды и тд и тп)

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        User user = USER_MANAGER.getUser(player);
        if (user == null) {
            return;
        }

        if (user.teleport(getHome())) {
            gamer.sendMessageLocale("ISLAND_TELEPORT_HOME", island.getOwner().getDisplayName());
            SOUND_API.play(player, SoundType.TELEPORT);
        }
    }

    public Location getHome() {
        if (home == null) {
            return island.getTerritory().getMiddleChunk().getMiddle();
        }

        return home;
    }

    @Override
    public void resetIsland() {
        IslandTerritory territory = island.getTerritory();
        Location locationHome = territory.getMiddleChunk().getMiddle();
        setHome(locationHome);
    }

    public void setHome(Location home) {
        final int id = island.getIslandID();
        if (id == -1)
            return;

        String loc = LocationUtil.locationToString(home, true);
        if (this.home == null) {
            DATABASE.execute("INSERT INTO `IslandHome` (`island`, `home`) VALUES (?, ?);", id, loc);
        }
        this.home = home;

        DATABASE.execute("UPDATE `IslandHome` SET `home` = ? WHERE `island`= ?;", loc, id);
    }
}
