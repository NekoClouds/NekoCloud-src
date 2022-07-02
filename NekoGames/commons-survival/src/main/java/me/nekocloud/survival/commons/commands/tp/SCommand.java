package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SCommand extends CommonsCommand {

    public SCommand(ConfigData configData) {
        super(configData, true, "s");
        setMinimalGroup(Group.MODERATOR);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "S_FORMAT");
            return;
        }

        Player other = Bukkit.getPlayer(strings[0]);
        if (other == null || !other.isOnline()) {
            COMMANDS_API.playerOffline(gamerEntity, strings[0]);
            return;
        }
        User user = USER_MANAGER.getUser(other);
        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user, UserTeleportByCommandEvent.Command.S,
                player.getLocation(), player);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        user.teleport(player.getLocation());
    }
}
