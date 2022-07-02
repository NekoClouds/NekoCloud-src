package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.util.LocationUtil;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class JumpCommand extends CommonsCommand {

    public JumpCommand(ConfigData configData) {
        super(configData, true, "jump");
        setMinimalGroup(configData.getInt("jumpCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        final Location location = player.getLocation();

        Location loc = LocationUtil.getTarget(player);
        loc.setYaw(location.getYaw());
        loc.setPitch(location.getPitch());
        loc.setY(loc.getY() + 1);

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.JUMP, loc);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        TeleportingUtil.teleport(player, this, () -> user.teleport(loc));
    }
}
