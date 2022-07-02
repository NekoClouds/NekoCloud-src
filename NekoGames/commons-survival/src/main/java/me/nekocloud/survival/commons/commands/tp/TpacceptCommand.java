package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;

public class TpacceptCommand extends TpaCommand {

    public TpacceptCommand(ConfigData configData) {
        super(configData, "tpaccept", "tpyes");
    }

    @Override
    protected void accept(Player sender, Player who) {
        User userWho = USER_MANAGER.getUser(who);
        CraftUser senderUser = (CraftUser) USER_MANAGER.getUser(sender);
        BukkitGamer senderGamer = GAMER_MANAGER.getGamer(sender);
        BukkitGamer gamerWho = GAMER_MANAGER.getGamer(who);

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(userWho,
                UserTeleportByCommandEvent.Command.TPACCEPT,
                sender.getLocation(),
                sender);
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        sendMessageLocale(senderGamer, "TPACCEPT_SENDER", who.getDisplayName());
        sendMessageLocale(gamerWho, "TPACCEPT_WHO", sender.getDisplayName());

        senderUser.getCallReguests().remove(who.getName());

        TeleportingUtil.teleport(who, this, () -> userWho.teleport(sender.getLocation()));
    }
}
