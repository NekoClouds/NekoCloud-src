package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.api.events.UserChangeGodModeEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GodCommand extends CommonsCommand {

    public GodCommand(ConfigData configData) {
        super(configData, true, "god");
        setMinimalGroup(configData.getInt("godCommand"));
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
            boolean god = !user.isGod();
            setGod(user, god);
            if (god) {
                send("GOD_ENABLE", gamerTo, null);
                send("GOD_ENABLE", gamer, player.getDisplayName());
            } else {
                send("GOD_DISABLE", gamer, player.getDisplayName());
                send("GOD_DISABLE", gamerTo, null);
            }
        } else {
            User user = USER_MANAGER.getUser(sender);
            boolean god = !user.isGod();
            setGod(user, god);
        }
    }

    private void setGod(User user, boolean god) {
        UserChangeGodModeEvent event = new UserChangeGodModeEvent(user, god);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        user.setGod(god, true);
    }
}
