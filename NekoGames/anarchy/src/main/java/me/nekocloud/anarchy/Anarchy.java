package me.nekocloud.anarchy;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import me.nekocloud.anarchy.command.AnarchystartCommand;
import me.nekocloud.anarchy.command.MoneyCommand;
import me.nekocloud.anarchy.gui.AnarchyMenuGui;
import me.nekocloud.anarchy.listener.PlayerListener;
import me.nekocloud.anarchy.listener.ScavengerListener;
import me.nekocloud.anarchy.stats.StatsLoader;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.utils.core.RestartServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Anarchy extends JavaPlugin {

    private AnarchyConfig config;

    private final TIntObjectMap<AnarchyMenuGui> menus = new TIntObjectHashMap<>();

    @Override
    public void onEnable() {
        config = new AnarchyConfig(this);

        StatsLoader.init();

        new RestartServer("05:00");

        for (Language language : Language.values()) {
            menus.put(language.getId(), new AnarchyMenuGui(language));
        }

        new PlayerListener(this);
        new ScavengerListener(this);

        new MoneyCommand();
        new AnarchystartCommand(menus);
    }

    public AnarchyConfig getAnarchyConfig() {
        return config;
    }

    @Override
    public FileConfiguration getConfig() {
        return getAnarchyConfig().getConfig();
    }
}
