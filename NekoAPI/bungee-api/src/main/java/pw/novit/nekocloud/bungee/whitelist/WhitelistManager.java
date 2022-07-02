package pw.novit.nekocloud.bungee.whitelist;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.Language;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class WhitelistManager { // cringe

    boolean enable;
    Configuration CONFIG;

    final Collection<String> playerNames = new ArrayList<>();

    public void load() {
        CONFIG = loadConfig();

        enable = CONFIG.getBoolean("whitelist.ENABLE");

        playerNames.clear();
        playerNames.addAll(CONFIG.getStringList("whitelist.PLAYERS"));
    }

    public @NotNull String getKickMessage(@NotNull Language lang) {
        return Joiner.on("\n").join(lang.getList("WHITELIST_KICK_MESSAGE"));
    }

    public @NotNull String getMotd() {
        return Joiner.on("\n").join(CONFIG.getStringList("whitelist.MOTD"));
    }

    public @NotNull String getHoverText() {
        return Joiner.on("\n").join(CONFIG.getStringList("whitelist.HOVER"));
    }

    public void setEnable(boolean enable) {
        this.enable = enable;

        CONFIG.set("whitelist.ENABLE", enable);
        saveConfig();
    }

    public void addPlayer(@NotNull String playerName) {
        playerNames.add(playerName.toLowerCase(Locale.ROOT));
        CONFIG.set("whitelist.PLAYERS", playerNames);

        saveConfig();
    }

    public void removePlayer(@NotNull String playerName) {
        playerNames.remove(playerName.toLowerCase(Locale.ROOT));
        CONFIG.set("whitelist.PLAYERS", playerNames);

        saveConfig();
    }


    Configuration loadConfig() {
        val config = new File(NekoBungeeAPI.getInstance().getDataFolder(), "whitelist.yml");
        Configuration cfg = null;
        try {
            if (!config.exists()) {
                Files.copy(NekoBungeeAPI.getInstance().getResourceAsStream("whitelist.yml"), config.toPath());
            }
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cfg;
    }

    void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(CONFIG,
                    new File(NekoBungeeAPI.getInstance().getDataFolder(), "whitelist.yml"));

        } catch (IOException e) {
            NekoBungeeAPI.getInstance().getLogger().severe("Error to save whitelist.yml - ");
            e.printStackTrace();
        }
    }
}

