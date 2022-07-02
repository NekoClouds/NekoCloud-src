package me.nekocloud.nekoapi.utils.bukkit;

import lombok.experimental.UtilityClass;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

@UtilityClass
public class BukkitUtil {

    public void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public  void runTaskLater(long delay, Runnable runnable){
        Bukkit.getScheduler().runTaskLater(NekoAPI.getInstance(), runnable, delay);
    }

    public void runTaskLaterAsync(long delay, Runnable runnable){
        Bukkit.getScheduler().runTaskLaterAsynchronously(NekoAPI.getInstance(), runnable, delay);
    }

    public void runTask(Runnable runnable){
        Bukkit.getScheduler().runTask(NekoAPI.getInstance(), runnable);
    }

    public void runTaskAsync(Runnable runnable){
        Bukkit.getScheduler().runTaskAsynchronously(NekoAPI.getInstance(), runnable);
    }

}
