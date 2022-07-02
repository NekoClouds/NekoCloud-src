package me.nekocloud.survival.commons.commands.info;

import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.entity.Player;

public class EnderChestCommand extends CommonsCommand {

    public EnderChestCommand(ConfigData configData) {
        super(configData, true, "enderchest", "ender", "ec", "chest");
        spigotCommand.setGroup(Group.getGroupByLevel(configData.getInt("enderChestCommand")));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();
        player.openInventory(player.getEnderChest());
    }
}
