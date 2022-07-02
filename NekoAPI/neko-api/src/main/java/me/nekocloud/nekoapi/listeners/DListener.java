package me.nekocloud.nekoapi.listeners;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DListener<T extends JavaPlugin> implements Listener {

    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    protected final T javaPlugin;

    protected DListener(final T javaPlugin) {
        this.javaPlugin = javaPlugin;
        Bukkit.getPluginManager().registerEvents(this, javaPlugin);
    }

    public void unregisterListener() {
        HandlerList.unregisterAll(this);
    }
}
