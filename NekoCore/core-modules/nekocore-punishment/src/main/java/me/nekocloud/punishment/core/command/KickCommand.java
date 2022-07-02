package me.nekocloud.punishment.core.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.punishment.PunishmentType;
import me.nekocloud.punishment.core.PunishmentManager;
import me.nekocloud.punishment.core.api.event.PunishmentGiveEvent;

import java.util.Arrays;

/**
 * @author xwhilds
 */
public final class KickCommand extends CommandExecutor {

    public KickCommand() {
        super("kick", "кик");

        setGroup(Group.JUNIOR);
        setOnlyPlayers(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, используй: §d/kick <ник> <причина>");
            return;
        }

        // проверяем наличие идентификатора у нарушителя
        if (!hasIdentifier(sender, args[0])) return;

        val owner = (CorePlayer) sender;
        val intruder = getCore().getPlayer(args[0]);
        if (intruder == null) {
            playerOffline(sender, args[0]);
            return;
        }

        val reason = ChatColor.translateAlternateColorCodes('&',
                Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        for (val staffCorePlayer : getCore().getOnlinePlayers(IBaseGamer::isStaff)) {
            staffCorePlayer.sendMessage("§d§lМОДЕРАЦИЯ §8| " + sender.getDisplayName() + " §fкикнул игрока "
                    + owner.getDisplayName() + " §fпо причине: §f" + reason);
        }

        getCore().callEvent(new PunishmentGiveEvent(owner, intruder, PunishmentType.KICK, reason));
        PunishmentManager.INSTANCE.kickPlayer(owner, intruder, reason);
    }
}
