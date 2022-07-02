package me.nekocloud.survival.commons.commands.home;

import com.google.common.collect.ImmutableList;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveHomeCommand extends CommonsCommand implements CommandTabComplete {

    public RemoveHomeCommand(ConfigData configData) {
        super(configData, true, "delhome", "removehome");
        spigotCommand.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX","DELHOME_FORMAT");
            return;
        }

        String nameHome = args[0].toLowerCase();
        if (user.getHomes().containsKey(nameHome)) {
            sendMessageLocale(gamerEntity, "HOME_REMOVE", nameHome);
            user.removeHome(nameHome);
            return;
        }

        gamerEntity.sendMessageLocale("HOME_NOT_FOUND", nameHome);
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(user.getHomes().keySet(), strings);
    }
}
