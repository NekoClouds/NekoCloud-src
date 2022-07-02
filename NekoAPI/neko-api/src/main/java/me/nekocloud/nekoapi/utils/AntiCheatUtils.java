package me.nekocloud.nekoapi.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@UtilityClass
public class AntiCheatUtils {

    public void unloadAndLoad() {
        unloadAndLoad("Matrix");
        unloadAndLoad("ReflexMoonlightLibs");
        unloadAndLoad("Reflex");
        unloadAndLoad("BanHammer");
    }

    private void unloadAndLoad(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
        }
    }

    //todo WTF
}
