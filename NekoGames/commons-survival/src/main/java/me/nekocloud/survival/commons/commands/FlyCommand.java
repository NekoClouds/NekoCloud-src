package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserChangeFlyStatusEvent;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyCommand extends CommonsCommand {

    public FlyCommand(ConfigData configData) {
        super(configData, true, "fly", "efly");
        setMinimalGroup(configData.getInt("flyCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = ((BukkitGamer) gamerEntity);
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup().getLevel() >= Group.ADMIN.getLevel()) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            BukkitGamer gamerTo = GAMER_MANAGER.getGamer(player);
            User user = USER_MANAGER.getUser(player);
            boolean fly = !user.isFly();

            setFly(user, fly);

            if (fly) {
                send("FLY_ENABLE", gamerTo, null);
                send("FLY_ENABLE", gamer, player.getDisplayName());
            } else {
                send("FLY_DISABLE", gamer, player.getDisplayName());
                send("FLY_DISABLE", gamerTo, null);
            }
        } else {
            User user = USER_MANAGER.getUser(sender);
            boolean fly = !user.isFly();
            setFly(user, fly);
        }
    }

    private void setFly(User user, boolean fly) {
        UserChangeFlyStatusEvent event = new UserChangeFlyStatusEvent(user, fly);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        user.setFly(fly, true);
    }
}
