package me.nekocloud.skyblock;

import lombok.Getter;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.nekoapi.achievements.manager.AchievementManager;
import me.nekocloud.nekoapi.listeners.JoinListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.ArmorStandUtil;
import me.nekocloud.nekoapi.utils.bukkit.EmptyWorldGenerator;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import me.nekocloud.skyblock.achievement.AchievementListener;
import me.nekocloud.skyblock.achievement.IslandAchievements;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.command.*;
import me.nekocloud.skyblock.dependencies.DependManager;
import me.nekocloud.skyblock.gui.GuiListener;
import me.nekocloud.skyblock.listener.*;
import me.nekocloud.skyblock.utils.FaweUtils;
import me.nekocloud.skyblock.utils.IslandLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class SkyBlock extends JavaPlugin {

    private ConfigManager configManager;

    private DependManager dependManager;
    private AchievementManager achievementManager;

    @Override
    public void onEnable() {
        EmptyWorldGenerator generator = NekoAPI.getInstance().getGenerator();
        Bukkit.createWorld(WorldCreator.name(SkyBlockAPI.getSkyBlockWorldName())
                .generator(generator)
                .generateStructures(false));

        World pvpWorld = Bukkit.createWorld(WorldCreator.name("PvPWorld")
                .generator(generator)
                .generateStructures(false));

        File file = new File(CoreUtil.getConfigDirectory() + "/skyConfig.yml");
        if (!file.exists()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§cКонфиг не найден, кажется некоторые вещи работать не будут и плагин будет отключен");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        configManager = new ConfigManager(file);

        IslandLoader.init();
        dependManager = new DependManager(this);

        achievementManager = new AchievementManager(this,
                "skyblock",
                ConnectionConstants.DOMAIN.getValue());
        achievementManager.setLoadOnJoin(true);
        achievementManager.addAchievements(IslandAchievements.getAchievements());

        new JoinListener(this);
        new ProtectLobbyListener(this);
        new PvpListener(this, pvpWorld);

        new ModuleListener(this);
        new SkyGamerListener(this);
        new ProtectListener(this);
//        new RedstoneFixListener(this);
        new IslandMainListener(this);
        new FlagsListener(this);
        new EventRewrite(this);
        new AchievementListener(this);

        new GuiListener(this, new IslandsCommand());

        new IslandCommand();
        new HomeCommand();
        new AcceptCommand();
        new CancelCommand();
        new AchievementCommand(achievementManager); //todo удалить вместе с классом

        ArmorStandUtil.fixArmorStand();

        FaweUtils.disableCommand(this);
    }

    @Override
    public void onDisable() {
        //World world = SkyBlockAPI.getSkyBlockWorld();
        //world.save();

        //Closer closer = Closer.create();
        //try {
        //    closer.register(guiListener);
        //    closer.register(dependManager);

        //    closer.close();
        //} catch (Throwable ex) {
        //    ex.printStackTrace();
        //}
        IslandLoader.close();
    }

    @Override
    public void reloadConfig() {
        configManager.reloadConfig();
        dependManager.loadAllConfigs();
    }

    @Override
    public FileConfiguration getConfig() {
        return configManager.getConfig();
    }
}
