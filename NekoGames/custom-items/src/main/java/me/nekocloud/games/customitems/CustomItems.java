package me.nekocloud.games.customitems;

import me.nekocloud.games.customitems.commands.CustomItemsCommand;
import me.nekocloud.games.customitems.loader.CustomItemsLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItems extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        CustomItemsLoader.loadItems(this);
        new CustomItemsCommand();
    }
}
