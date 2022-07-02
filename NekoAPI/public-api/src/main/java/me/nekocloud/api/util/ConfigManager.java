package me.nekocloud.api.util;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.Spigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class ConfigManager {

    private static final Spigot SPIGOT = NekoCloud.getGamerManager().getSpigot();

    private final File file;
    private FileConfiguration config;

    public ConfigManager(@NotNull File file) {
        this.file = file;
        loadConfig();
    }

    public ConfigManager(@NotNull File file,@NotNull  String configName) {
        this.file = new File(file, configName);
        loadConfig();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    private void loadConfig() {
        if (!file.exists())
            saveDefaultConfig();

        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void saveDefaultConfig() {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            SPIGOT.sendMessage("§cНе создан файл " + file.getName()
                    + " или же путь " + file.getPath());
        }
        assert in != null;
        try {
            if (!file.exists()) {
                int len;
                FileOutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);

                out.close();
                in.close();
            } else {
                SPIGOT.sendMessage("Невозможно сохранить " + file.getName() + " в " + file
                        + ", потому что " + file.getName() + " уже создан");
            }
        } catch (IOException e) {
            SPIGOT.sendMessage("§cНевозможно сохранить " + file.getName() + " в " + file);
        }
    }
}
