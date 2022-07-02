package me.nekocloud.rofl;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;

import java.io.File;
import java.util.Collection;

public class AntiPidorCommand implements CommandInterface {

    private final AntiPidor antiPidorPlugin;

    public AntiPidorCommand(AntiPidor antiPidorPlugin) {
        val command = COMMANDS_API.register("pidreload", this);
        command.setGroup(Group.ADMIN);

        this.antiPidorPlugin = antiPidorPlugin;
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        Collection<String> pidorList = antiPidorPlugin.getPidorListener().getPidorList();
        File file = new File(antiPidorPlugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            antiPidorPlugin.saveDefaultConfig();
        }

        antiPidorPlugin.reloadConfig();
        pidorList.clear();
        pidorList.addAll(antiPidorPlugin.getConfig().getStringList("pidor-list"));
        gamerEntity.sendMessage("Конфиг перезагружен!");
    }
}
