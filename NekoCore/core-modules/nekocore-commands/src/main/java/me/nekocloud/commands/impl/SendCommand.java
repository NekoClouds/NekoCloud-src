package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.server.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SendCommand extends CommandExecutor {

    public SendCommand() {
        super("send", "playersend", "sendplayer", "coresend");

        setGroup(Group.BUILDER);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessageLocale("SEND_HELP");
            return;
        }

        val targetPlayer = NekoCore.getInstance().getPlayer(args[0]);
        if (targetPlayer == null) {
            playerOffline(sender, args[0]);
            return;
        }

        val bukkitServer = NekoCore.getInstance().getBukkit(args[1]);
        if (bukkitServer == null) {
            sender.sendMessageLocale("SERVER_IS_NOT_ONLINE");
            return;
        }

        if (args[1].startsWith("@") && args[1].length() > 1) {
            Collection<Bukkit> servers = NekoCore.getInstance().getServersByPrefix(args[1].substring(1));
            for (val bukkitServers : servers) {
                targetPlayer.redirect(bukkitServers);
            }
        } else {
            targetPlayer.redirect(bukkitServer);
        }

        sender.sendMessageLocale("SENDING_PLAYER_TO_SERVER", targetPlayer.getDisplayName(), bukkitServer.getName());
        targetPlayer.sendMessageLocale("YOU_HAVE_BEEN_SENT", sender.getDisplayName(), bukkitServer.getName());
    }

}
