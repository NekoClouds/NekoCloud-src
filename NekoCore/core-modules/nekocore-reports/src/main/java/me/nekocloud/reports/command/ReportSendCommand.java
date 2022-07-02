package me.nekocloud.reports.command;


import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.reports.ReportManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ReportSendCommand extends CommandExecutor {
    public ReportSendCommand() {
        super("report", "жалоба", "читак");
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§d§lЖАЛОБЫ §8| §fОшибка, пишите /report <ник> <причина>");
            return;
        }

        CorePlayer player = (CorePlayer) sender;
        CorePlayer targetPlayer = NekoCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage("§d§lЖАЛОБЫ §8| §fОшибка, Данный игрок не в сети!");
            return;
        }
        if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
            player.sendMessage("§d§lЖАЛОБЫ §8| §fОшибка, Жаловаться на себя нельзя");
            return;
        }
        if (ReportManager.INSTANCE.hasReport(player.getName(), targetPlayer.getName())) {
            player.sendMessage("§d§lЖАЛОБЫ §8| §fОшибка, вы уже жаловались на этого игрока");
            return;
        }

        String report = args[1];

        if (report.length() > 16) {
            player.sendMessage("§d§lЖАЛОБЫd §8| §fОшибка, причина не может быть больше 16 символов");
            return;
        }

        String reportReason = Joiner.on(" ").join(Arrays.copyOfRange((Object[])args, 1, args.length));
        player.sendMessage("§d§lЖАЛОБЫ §8| §fВы успешно отправили жалобу на игрока " + targetPlayer.getDisplayName());
        ReportManager.INSTANCE.createReport(targetPlayer, player, reportReason);
        if (NekoCore.getInstance().getOnlinePlayers(IBaseGamer::isStaff).isEmpty())
            player.sendMessage("§d§lЖАЛОБЫ §8| §fНа данный момент из персонала в сети не кого нет, так что сорян, ебись оно конем :(");
    }
}
