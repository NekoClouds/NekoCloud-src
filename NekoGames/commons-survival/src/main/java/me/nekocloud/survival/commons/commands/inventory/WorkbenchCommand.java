package me.nekocloud.survival.commons.commands.inventory;

import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class WorkbenchCommand extends CommonsCommand {

    public WorkbenchCommand(ConfigData configData) {
        super(configData, true, "workbench", "wb");
        setMinimalGroup(configData.getInt("workBenchCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        player.openWorkbench(null, true);
    }
}
