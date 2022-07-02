package me.nekocloud.games.trader;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.games.trader.commands.TraderCommand;
import me.nekocloud.games.trader.gui.TraderGui;
import me.nekocloud.games.trader.listener.TraderListener;
import me.nekocloud.games.trader.manager.TraderManager;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class Trader extends JavaPlugin {

    private TraderManager traderManager;
    private HumanNPC traderNpc;
    private FileConfiguration config;

    @Getter
    private static Trader instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        val file = new File(CoreUtil.getConfigDirectory() + "/trader.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning("§c[Trader] [ERROR] §fКонфиг не найден! Плагин работать не будет!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        val configManager = new ConfigManager(file);
        config = configManager.getConfig();

        val location = LocationUtil.stringToLocation(config.getString("npc"), true);
        traderNpc = NekoCloud.getEntityAPI().createNPC(location, Skin.SKIN_SHOP);
        traderNpc.setGlowing(ChatColor.DARK_BLUE);
        traderNpc.setPublic(true);

        NekoCloud.getGuiManager().createGui(TraderGui.class);

        traderManager = new TraderManager();
        traderManager.load(this);

        new TraderCommand();
        new TraderListener(this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, traderManager, 5, 3600*20);
    }
}
