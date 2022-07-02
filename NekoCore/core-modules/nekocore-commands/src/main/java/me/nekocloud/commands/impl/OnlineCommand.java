package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class OnlineCommand extends CommandExecutor {

    public OnlineCommand() {
        super("online", "coreonline", "онлайн");
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length == 0) {
            val online = NekoCore.getInstance().getOnline();
            sender.sendMessageLocale("ONLINE_ALL_PLAYERS_ONLINE",
                    NumberUtils.spaced(online),
                    CommonWords.PLAYERS_1.convert(online, sender.getLanguage()));
            return;
        }

        val serverManager = NekoCore.getInstance().getServerManager();
        if (args[0].startsWith("@") && args[0].length() > 1) {
            val serverPrefix = args[0].substring(1);
            val online = NekoCore.getInstance().getOnlineByServerPrefix(serverPrefix);
            sender.sendMessageLocale("ONLINE_SERVERS", serverPrefix,
                    NumberUtils.spaced(online),
                    CommonWords.PLAYERS_1.convert(online, sender.getLanguage()));
            return;
        }

        val bukkitServer = serverManager.getBukkit(args[0]);
        if (bukkitServer == null) {
            sender.sendMessageLocale("SERVER_IS_NOT_ONLINE");
            return;
        }

        val online = bukkitServer.getOnline();
        sender.sendMessageLocale("ONLINE_SERVER", bukkitServer.getName(),
                NumberUtils.spaced(online),
                CommonWords.PLAYERS_1.convert(online, sender.getLanguage()));
    }

}
