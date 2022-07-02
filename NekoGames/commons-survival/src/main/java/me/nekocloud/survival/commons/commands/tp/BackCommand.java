package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BackCommand extends CommonsCommand {

    public BackCommand(ConfigData configData) {
        super(configData, true, "back", "return");
        setMinimalGroup(configData.getInt("backCommand"));
        spigotCommand.setCooldown(10 * 60, getCooldownType());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        Location last = CommonsSurvivalAPI.getSpawn();
        if (user.getLastLocation() != null && user.getLastLocation().getY() > 0)
            last = user.getLastLocation();

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user, UserTeleportByCommandEvent.Command.BACK,
                user.getLastLocation());
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        Location finalLast = last;
        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(finalLast)) {
                sendMessageLocale(gamerEntity, "BACK");
            }
        });
    }
}
