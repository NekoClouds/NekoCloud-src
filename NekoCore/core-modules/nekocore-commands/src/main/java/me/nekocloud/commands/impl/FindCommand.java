package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class FindCommand extends CommandExecutor {

    public FindCommand() {
        super("corefind", "find", "search");
        setOnlyAuthorized(true);
    }


    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length == 0) {
            notEnoughArguments(sender, "SERVER_PREFIX", "FIND_FORMAT");
            return;
        }

        val name = args[0];
        if (!hasIdentifier(sender, name)) return;
        CorePlayer targetPlayer = NekoCore.getInstance().getPlayer(name);
        if (targetPlayer == null) {
            targetPlayer = NekoCore.getInstance().getOfflinePlayer(name);

            val lastOnline = TimeUtil.leftTime(sender.getLanguage(),
                    System.currentTimeMillis() - targetPlayer.getLastOnline());
            sender.sendMessageLocale("FIND_RESULT_OFFLINE",
                    targetPlayer.getDisplayName(), lastOnline);
            return;
        }

        sender.sendMessageLocale("FIND_RESULT", targetPlayer.getDisplayName(),
                targetPlayer.getBukkit().getName());
    }

}
