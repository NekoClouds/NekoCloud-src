package me.nekocloud.punishment.core.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.punishment.PunishmentType;
import me.nekocloud.punishment.core.PunishmentManager;
import me.nekocloud.punishment.core.api.event.PunishmentGiveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author xwhilds
 */
public final class BanCommand extends CommandExecutor {

    private final PunishmentManager manager = PunishmentManager.INSTANCE;

    public BanCommand() {
        super("ban", "tempban", "бан", "съебал");

        setGroup(Group.JUNIOR);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, используй: §5/ban <ник> <время|-e|-1> <причина>");
            return;
        }

        // проверка наличия идентификатора нарушителя
        if (!hasIdentifier(sender, args[0])) return;

        val owner = (CorePlayer) sender;
        val intruder = getCore().getOfflinePlayer(args[0]);
        // проверка наличия прав для наказания навсегда
        if (!intruder.getGroup().equals(owner.getGroup())) {
            owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, ты не можешь забанить данного игрока, " +
                    "так как он выше тебя по статусу!");
            return;
        }

        if ((args[1].contains("-e") || args[1].contains("-1")) && !owner.isStModerator()) {
            owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, для выдачи бана навсегда, у тебя не хватает полномочий!");
            return;
        }

        val reason = ChatColor.translateAlternateColorCodes('&',
                Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        PunishmentType type = PunishmentType.TEMP_MUTE;
        val banTimeMillis = TimeUtil.parseTimeToMillis(args[1], TimeUnit.MILLISECONDS);
        if (banTimeMillis == -1) {
            type = PunishmentType.PERMANENT_MUTE;
        }

        val punishmentTimeVerb = (type.equals(PunishmentType.PERMANENT_MUTE) ? ("§fна §d"
                + TimeUtil.leftTime(owner.getLanguage(), banTimeMillis)) : "§cнавсегда");

        for (val staffPlayer : getCore().getOnlinePlayers(IBaseGamer::isStaff)) {
            staffPlayer.sendMessage("§d§lМОДЕРАЦИЯ §8| " + owner.getDisplayName() + " §fзабанил игрока "
                    + intruder.getDisplayName() + punishmentTimeVerb + " §dпо причине: §f" + reason);
        }

        owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fТы успешно забанил " + intruder.getDisplayName()
                + " §dнавсегда с причиной: §5" + reason);

        getCore().callEvent(new PunishmentGiveEvent(owner, intruder, type, reason));
        manager.givePunishmentToPlayer(
                owner, intruder,
                type,
                reason, banTimeMillis);
    }
}
