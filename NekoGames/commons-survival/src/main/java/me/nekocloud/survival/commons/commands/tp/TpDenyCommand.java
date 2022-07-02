package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

public class TpDenyCommand extends TpaCommand {

    public TpDenyCommand(ConfigData configData) {
        super(configData, "tpdeny", "tpno");
    }

    @Override
    protected void accept(Player sender, Player who) {
        User user = CommonsCommand.USER_MANAGER.getUser(sender);
        if (user == null)
            return;

        ((CraftUser)user).getCallReguests().remove(who.getName());
        BukkitGamer gamerSender = CommonsCommand.GAMER_MANAGER.getGamer(sender);
        BukkitGamer gamerWho = CommonsCommand.GAMER_MANAGER.getGamer(who);

        if (gamerSender != null)
            sendMessageLocale(gamerSender, "TPDENY", who.getDisplayName());

        if (gamerWho != null)
            sendMessageLocale(gamerWho, "TPDENY_YOU", sender.getDisplayName());

    }
}
