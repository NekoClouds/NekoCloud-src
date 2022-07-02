package me.nekocloud.punishment.core.command;

import lombok.val;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.punishment.PunishmentType;
import me.nekocloud.punishment.core.PunishmentManager;
import me.nekocloud.punishment.core.api.event.PunishmentClearEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author xwhilds
 */
public final class UnbanCommand extends CommandExecutor {

    private final PunishmentManager manager = PunishmentManager.INSTANCE;

    public UnbanCommand() {
        super("unban", "разбан");

        setGroup(Group.JUNIOR);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, используй: §d/unban <ник>");
            return;
        }

        if (!hasIdentifier(sender, args[0])) return;
        val owner = (CorePlayer) sender;
        val intruder = getCore().getOfflinePlayer(args[0]);

        if (manager.getPlayerPunishment(intruder.getName(), PunishmentType.PERMANENT_BAN) == null ||
            manager.getPlayerPunishment(intruder.getName(), PunishmentType.TEMP_BAN) == null)
            owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, игрок " + intruder.getDisplayName() +
                    "§f не заблокирован! Если он переподключится, то он будет разбанен.");
        else {
            owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fТы успешно разбанил " + intruder.getDisplayName());
            for (val staffPlayer : getCore().getOnlinePlayers(IBaseGamer::isStaff)) {
                staffPlayer.sendMessage("§d§lМОДЕРАЦИЯ §8| " + owner.getDisplayName()
                        // TODO @xwhilds: получать какой был тип бана(временный, навсегда)
                        + "§f разбанил игрока " + intruder.getDisplayName());
            }
        }

        // TODO @xwhilds: сохрнять в историю разбан
        getCore().callEvent(new PunishmentClearEvent(owner, intruder, PunishmentType.PERMANENT_BAN));
        manager.unbanPlayer(owner, intruder);
    }

}
