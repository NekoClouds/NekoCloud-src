package me.nekocloud.lobby.commands;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.lobby.config.SettingConfig;

public class SpawnCommand implements CommandInterface {

    private final SettingConfig settingConfig;

    public SpawnCommand(SettingConfig settingConfig) {
        this.settingConfig = settingConfig;

        SpigotCommand spigotCommand = COMMANDS_API.register("spawn", this, "home");
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        ((BukkitGamer) gamerEntity).getPlayer().teleport(settingConfig.getSpawn());
    }
}
