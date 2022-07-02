package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpawnCommand extends CommonsCommand {

    public SpawnCommand(ConfigData configData) {
        super(configData, true, "spawn");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup().getLevel() >= Group.ADMIN.getLevel()) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            send("SPAWN", gamer, player.getDisplayName());
            spawn(player);
            return;
        }

        TeleportingUtil.teleport(sender, this, () -> spawn(sender));
    }

    private void spawn(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        User user = USER_MANAGER.getUser(player);
        if (gamer == null || user == null)
            return;

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.SPAWN, CommonsSurvivalAPI.getSpawn());
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (user.teleport(CommonsSurvivalAPI.getSpawn())) {
            send("SPAWN", gamer, null);
        }
    }
}
