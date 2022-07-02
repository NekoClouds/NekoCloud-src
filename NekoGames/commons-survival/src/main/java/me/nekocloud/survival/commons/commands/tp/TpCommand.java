package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpCommand extends CommonsCommand {

    public TpCommand(ConfigData configData) {
        super(configData,true, "tp", "teleport");
        setMinimalGroup(configData.getInt("tpCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "TP_FORMAT");
            return;
        }

        String name = strings[0];
        Player player = Bukkit.getPlayer(name);
        if (player == null || !player.isOnline()) {
            COMMANDS_API.playerOffline(gamerEntity, name);
            return;
        }
        User userTo = USER_MANAGER.getUser(player);
        BukkitGamer gamerTo = GAMER_MANAGER.getGamer(player);
        if (userTo == null || gamerTo == null)
            return;

        if (!userTo.isTpToggle() && !gamer.getFriends().containsKey(gamerTo.getPlayerID()) && !gamer.isModerator()) {
            gamerEntity.sendMessageLocale("TPTOGGLE", player.getDisplayName());
            return;
        }

        if (strings.length > 1 && gamer.isStaff()) {
            String nameTo = strings[0];
            name = strings[1];
            sender = Bukkit.getPlayer(nameTo);
            player = Bukkit.getPlayer(name);
            if (sender == null || !sender.isOnline()) {
                CommandInterface.COMMANDS_API.playerOffline(gamerEntity, nameTo);
                return;
            }

            if (player == null || !player.isOnline()) {
                CommandInterface.COMMANDS_API.playerOffline(gamerEntity, nameTo);
                return;
            }

            if (!sender.getName().equals(player.getName()))
                sendMessageLocale(gamerEntity, "TELEPORT_TO", sender.getDisplayName(), player.getDisplayName());

        }

        teleport(sender, player);
    }

    private void teleport(Player player, Player to) {
        User user = USER_MANAGER.getUser(player);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (user == null || gamer == null)
            return;

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user, UserTeleportByCommandEvent.Command.TP,
                to.getLocation());
        BukkitUtil.callEvent(event);


        if (event.isCancelled()) {
            return;
        }

        if (user.teleport(to.getLocation())) {
            sendMessageLocale(gamer, "TELEPORT", to.getDisplayName());
        }
    }
}
