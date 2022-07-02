package me.nekocloud.commands.impl;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.command.sender.ConsoleCommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class IpCommand extends CommandExecutor {

    public IpCommand() {
        super("ip", "айпи", "ип");

        setGroup(Group.ADMIN);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        CorePlayer player;

        if (args.length == 0) {

            // Это если консоль пишет
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("§cОшибка, пишите - /ip <ник игрока>");
                return;
            }

            player = ((CorePlayer) sender);

        } else {

            player = NekoCore.getInstance().getPlayer(args[0]);
        }

        if (player == null) {
            playerOffline(sender, args[0]);
            return;
        }

        sender.sendMessage("§d§lNeko§f§lCloud §8| §fIP игрока " + player.getDisplayName() + " - §c" + player.getIp().getHostAddress());

//        IpAddressUtil.getAddressStats(player.getInetSocketAddress(),
//                (result, error) -> {
//
//                    if (error != null) {
//                        sender.sendMessage(ChatColor.RED + error.getCause().toString() + ": " + error.getMessage());
//                        return;
//                    }
//
//                    sender.sendMessage("§d§lNeko§f§lCloud §8| §fСтатистика IP адреса игрока:");
//                    sender.sendMessage("    §fТип адреса: §d" + result.type);
//                    sender.sendMessage("    §fГород: §d" + result.city);
//                    sender.sendMessage("    §fСтрана: §d" + result.country_name + " (" + result.location.capital + ")");
//                    sender.sendMessage("    §fКоординаты провайдера: §d" + result.latitude + ", " + result.longitude);
//                });
    }
}