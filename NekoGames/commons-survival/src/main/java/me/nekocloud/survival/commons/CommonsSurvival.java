package me.nekocloud.survival.commons;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import me.nekocloud.nekoapi.utils.core.RestartServer;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.commands.*;
import me.nekocloud.survival.commons.commands.get.HeadCommand;
import me.nekocloud.survival.commons.commands.get.KitCommand;
import me.nekocloud.survival.commons.commands.home.BedHomeCommand;
import me.nekocloud.survival.commons.commands.home.HomeCommand;
import me.nekocloud.survival.commons.commands.home.RemoveHomeCommand;
import me.nekocloud.survival.commons.commands.home.SetHomeCommand;
import me.nekocloud.survival.commons.commands.info.*;
import me.nekocloud.survival.commons.commands.inventory.AnvilCommand;
import me.nekocloud.survival.commons.commands.inventory.WorkbenchCommand;
import me.nekocloud.survival.commons.commands.time.PTimeCommand;
import me.nekocloud.survival.commons.commands.time.TimeCommand;
import me.nekocloud.survival.commons.commands.tp.*;
import me.nekocloud.survival.commons.commands.warp.*;
import me.nekocloud.survival.commons.commands.weather.PWeatherCommand;
import me.nekocloud.survival.commons.commands.weather.WeatherCommand;
import me.nekocloud.survival.commons.config.CommonsSurvivalSql;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.listener.GuiListener;
import me.nekocloud.survival.commons.listener.KitListener;
import me.nekocloud.survival.commons.listener.PlayerListener;
import me.nekocloud.survival.commons.listener.UserListener;
import me.nekocloud.survival.commons.util.GuiThread;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class CommonsSurvival extends JavaPlugin {

    private FileConfiguration config;
    private static ConfigData configData;

    @Getter
    private static CommonsSurvival instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configData = new ConfigData(this);
        CommonsSurvivalGui.setConfigData(configData);

        configData.load();
        CommonsSurvivalSql.init();
        configData.init();

        registerCommand();

        new UserListener(this);

        new GuiListener(this);
        new PlayerListener(this);

        if (configData.isKitSystem())
            new KitListener(this);

        new GuiThread();
    }

    @Override
    public void reloadConfig() {
        val file = new File(CoreUtil.getConfigDirectory() + "/commons.yml");
        if (!file.exists()) {
            Spigot spigot = NekoCloud.getGamerManager().getSpigot();
            spigot.sendMessage("§c[CommonsSurvival] Конфиг не найден! Плагин выключается");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        val configManager = new ConfigManager(file);
        config = configManager.getConfig();
    }

    @Override
    public void saveConfig() {
        //nothing
    }

    @Override
    public void saveDefaultConfig() {
        reloadConfig();

        if (config.contains("Restart")) {
            new RestartServer(config.getString("Restart"));
        }
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    @Override
    public void onDisable() {
        CommonsSurvivalSql.getMySqlDatabase().close();
    }

    private void registerCommand() {
        new RecipeCommand(configData);
        new FlyCommand(configData);
        new ClearCommand(configData);
        new ReloadCommand(this);
        new ExtCommand(configData);
        new HealCommand(configData);
        new SpeedCommand(configData);
        new SpawnCommand(configData);
        new ListCommand(configData);
        new NearCommand(configData);
        new GodCommand(configData);
        new FeedCommand(configData);
        new WorkbenchCommand(configData);
        new SuicideCommand(configData);
        new AnvilCommand(configData);
        new RepairCommand(configData);
        new EnderChestCommand(configData);

        // время
        new PTimeCommand(configData);
        new TimeCommand(configData);

        new GamemodeCommand(configData);
        new TpCommand(configData);
        new TopCommand(configData);
        new ItemdbCommand(configData);
        new SCommand(configData);
        new TpDenyCommand(configData);
        new BackCommand(configData);
        new JumpCommand(configData);
        new TpPosCommand(configData);
        new TpChunkCommand(configData);
        new HatCommand(configData);
        new HeadCommand(configData);

        new GiveCommand(configData);
//        new BroadcastCommand(configData);
//        new DispellCommand(configData);

        // погода
        new PWeatherCommand(configData);
        new WeatherCommand(configData);

//        new CompassCommand(configData);
//        new DepthCommand(configData);
//        new InvSeeCommand(configData);
//        new ItemRenameCommand(configData);
//        new GetPosCommand(configData);
//        new EnchantingTableCommand(configData);
        new AfkCommand(configData);

        if (configData.isWarpSystem()) {
            new WarpCommand(configData);
            new DelWarpCommand(configData);
            new PlayerWarpCommand(configData);
            new CreateWarpCommand(configData);
            new WarpInfoCommand(configData);
        }

        if (configData.isCallSystem()) {
            new CallCommand(configData);
            new TpToggleCommand(configData);
            new TpacceptCommand(configData);
        }

        if (configData.isKitSystem())
            new KitCommand(configData);


        if (configData.isHomeSystem()) {
            new HomeCommand(configData);
            new SetHomeCommand(configData);
            new RemoveHomeCommand(configData);
        }

        if (configData.isTrade())
            new TradeCommand(this);

        if (configData.isRtpSystem())
            new RtpCommand(configData);

        if (configData.isBedHomeSystem())
            new BedHomeCommand(configData);

    }
}
