package me.nekocloud.rofl;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class AntiPidor extends JavaPlugin {

    @Getter
    private PidorListener pidorListener;

    @Override
    public void onEnable() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveDefaultConfig();
        }

        pidorListener = new PidorListener(this);
        new AntiPidorCommand(this);
    }
}
