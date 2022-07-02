package pw.novit.nekocloud.bungee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.redis.Redis;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pw.novit.nekocloud.bungee.announcer.AnnounceManager;
import pw.novit.nekocloud.bungee.api.utils.BungeeUtil;
import pw.novit.nekocloud.bungee.commands.NekoBungeeCommand;
import pw.novit.nekocloud.bungee.commands.PingCommand;
import pw.novit.nekocloud.bungee.commands.SkinCommand;
import pw.novit.nekocloud.bungee.commands.WhitelistCommand;
import pw.novit.nekocloud.bungee.filter.config.FilterConfig;
import pw.novit.nekocloud.bungee.listeners.FilterListener;
import pw.novit.nekocloud.bungee.listeners.GamerListener;
import pw.novit.nekocloud.bungee.listeners.NekoAPIMessageListener;
import pw.novit.nekocloud.bungee.whitelist.WhitelistManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * #КодНовита
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class NekoBungeeAPI extends Plugin {
    static @Getter NekoBungeeAPI instance; {
        instance = this;
    }

    String serverName;
    IpProtect ipProtect;
    Configuration config;

    final AnnounceManager announceManager   = new AnnounceManager();
    final WhitelistManager whitelistManager = new WhitelistManager();

    @Override
    public void onEnable() {
        loadConfigs();

        registerListeners();
        registerCommands();
    }

    // Загрузка конфигов
    // Так надо :(
    public void loadConfigs() {
        config = loadConfig();
        ipProtect = new IpProtect(this);

        whitelistManager.load();
        announceManager.load();
    }

    // Рег слушателей
    void registerListeners() {
        new GamerListener(this);
    }

    // Рег команд
    void registerCommands() {
        new NekoBungeeCommand(this);
        new SkinCommand(this);
        new WhitelistCommand(this);
        new PingCommand(this);
    }

    // Основной конфиг
    Configuration loadConfig() {
        val config = new File(getInstance().getDataFolder(),"config.yml");
        Configuration cfg = null;
        try {
            if (!config.exists()) {
                Files.copy(getInstance().getResourceAsStream("config.yml"), config.toPath());
            }
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cfg;
    }
}
