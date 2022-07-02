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

public class TopCommand extends CommonsCommand {

    public TopCommand(ConfigData configData) {
        super(configData, true, "top");
        setMinimalGroup(configData.getInt("topCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        final int topX = player.getLocation().getBlockX();
        final int topZ = player.getLocation().getBlockZ();
        final float pitch = player.getLocation().getPitch();
        final float yaw = player.getLocation().getYaw();
        final Location loc;
        try {
            loc = LocationUtil.getSafeDestination(new Location(player.getWorld(), topX,
                    player.getWorld().getMaxHeight(), topZ, yaw, pitch));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.TOP, loc);
        BukkitUtil.callEvent(event);

        if (!event.isCancelled()) {
            TeleportingUtil.teleport(player, this, () -> user.teleport(loc));
        }

    }
}
