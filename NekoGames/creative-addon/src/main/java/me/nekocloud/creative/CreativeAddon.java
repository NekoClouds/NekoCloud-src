package me.nekocloud.creative;

import me.nekocloud.base.locale.Language;
import me.nekocloud.creative.command.CreativeMenuCommand;
import me.nekocloud.creative.command.WorldCommand;
import me.nekocloud.creative.gui.CreativeMenuGui;
import me.nekocloud.creative.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class CreativeAddon extends JavaPlugin {

    private final Map<Integer, CreativeMenuGui> menus = new HashMap<>();

    @Override
    public void onEnable() {

        for (Language language : Language.values()) {
            menus.put(language.getId(), new CreativeMenuGui(language));
        }

        new WorldCommand();
        new CreativeMenuCommand(menus);

        new PlayerListener(this);
    }

    public Map<Integer, CreativeMenuGui> getMenus() {
        return menus;
    }
}
