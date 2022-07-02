package me.nekocloud.skyblock.api.event.absract;

import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.skyblock.api.manager.IslandManager;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class IslandListener extends DListener<JavaPlugin> {

    protected static final IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();

    protected IslandListener(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    protected static boolean isSkyBlockWorld(World world) {
        return world != null && world.getName().equalsIgnoreCase(SkyBlockAPI.getSkyBlockWorldName());
    }

    protected static boolean isSkyBlockWorld(Location location) {
        return isSkyBlockWorld(location.getWorld());
    }
}
