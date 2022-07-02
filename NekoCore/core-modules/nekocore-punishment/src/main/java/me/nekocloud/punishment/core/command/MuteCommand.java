package me.nekocloud.punishment.core.command;

import com.google.common.base.Joiner;
import lombok.val;
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
public final class MuteCommand extends CommandExecutor {

    public MuteCommand() {
        super("mute", "tempmute", "мут", "завались");

        setGroup(Group.JUNIOR);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, используй - §5/mute <ник> <время|-e|-1> <причина>");
            return;
        }

        // проверка наличие идентификатора нарушителя
        if (!hasIdentifier(sender, args[0])) return;

        val owner = (CorePlayer) sender;
        val intruder = getCore().getOfflinePlayer(args[0]);
        // проверка наличия прав для наказания навсегда
        if (!intruder.getGroup().equals(owner.getGroup())) {
            owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, ты не можешь замутить данного игрока, " +
                    "так как он выше тебя по статусу!");
            return;
        }

        if ((args[1].contains("-e") || args[1].contains("-1")) && !owner.isStModerator()) {
            owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fОшибка, для выдачи мута навсегда, у тебя не хватает полномочий!");
            return;
        }

        PunishmentType type = PunishmentType.TEMP_MUTE;
        val muteTimeMillis = TimeUtil.parseTimeToMillis(args[1], TimeUnit.MILLISECONDS);
        if (muteTimeMillis == -1) {
            type = PunishmentType.PERMANENT_MUTE;
        }

        val punishmentTimeVerb = (type.equals(PunishmentType.PERMANENT_MUTE) ? ("§fна §d"
                + TimeUtil.leftTime(owner.getLanguage(), muteTimeMillis)) : "§cнавсегда");

        val reason = ChatColor.translateAlternateColorCodes('&',
                Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        owner.sendMessage("§d§lМОДЕРАЦИЯ §8| §fТы успешно замутил " + intruder.getDisplayName()
                +  punishmentTimeVerb + "§f по причине: §e" + reason);

        getCore().callEvent(new PunishmentGiveEvent(owner, intruder, type, reason));
        PunishmentManager.INSTANCE.givePunishmentToPlayer(
                owner, intruder,
                type,
                reason, muteTimeMillis);
    }
}
