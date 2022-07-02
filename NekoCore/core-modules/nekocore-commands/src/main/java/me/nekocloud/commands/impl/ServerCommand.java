package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ServerCommand extends CommandExecutor {

    public ServerCommand() {
        super("server", "serv", "сервер", "coreserver");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
        setGroup(Group.BUILDER);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        val player = (CorePlayer) sender;

        if (args.length == 0) {
            player.sendMessageLocale("SERVER_HELP");
            return;
        }

        val bukkitServer = NekoCore.getInstance().getBukkit(args[0]);
        if (bukkitServer == null) {
            player.sendMessageLocale("SERVER_IS_NOT_ONLINE", args[0]);
            return;
        }

        if (args[0].startsWith("@") && args[0].length() > 1) {
            Collection<Bukkit> servers = NekoCore.getInstance().getServersByPrefix(args[1].substring(1));
            for (val bukkitServer1 : servers) {
                player.redirect(bukkitServer1);
            }
        } else {
            player.redirect(bukkitServer);
        }
    }

}
