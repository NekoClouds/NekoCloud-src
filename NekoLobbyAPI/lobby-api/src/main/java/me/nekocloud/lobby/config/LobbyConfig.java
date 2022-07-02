package me.nekocloud.lobby.config;

import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public abstract class LobbyConfig {

    private static final Spigot SPIGOT = NekoCloud.getGamerManager().getSpigot();

    ConfigManager configManager;

    @Getter
    protected final Lobby lobby;

    LobbyConfig(Lobby lobby, String fileName) {
        this.lobby = lobby;
        File file = new File(CoreUtil.getConfigDirectory() + "/" + fileName + ".yml");
        if (!file.exists()) {
            SPIGOT.sendMessage("§c[LOBBY-API] Конфиг " + fileName + " не найден, кажется некоторые вещи работать не будут");
            return;
        }
        this.configManager = new ConfigManager(file);
    }

    public abstract void load();

    public abstract void init();

    public FileConfiguration getConfig() {
        return configManager.getConfig();
    }
}
