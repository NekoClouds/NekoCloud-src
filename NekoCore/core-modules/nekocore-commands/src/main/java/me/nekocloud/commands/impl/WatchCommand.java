package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class WatchCommand extends CommandExecutor {

    public WatchCommand() {
        super("watch", "спек", "спектатор", "spectate", "spec");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
        setGroup(Group.JUNIOR);
        setCooldown(1, "watch_command");
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        final CorePlayer player = (CorePlayer) sender;

        if (args.length == 0) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fИспользуйте - §e/watch <ник>");
            return;
        }

        final String name = args[0];
        CorePlayer targetPlayer = NekoCore.getInstance().getPlayer(name);
        if (targetPlayer == null) {
            playerOffline(sender, name);
            return;
        }

        if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fОшибка, вы не можете следить за собой.");
            return;
        }

        val targetServer = player.getBukkit();
        if (targetServer.getName().equals(player.getBukkit().getName())) {
            return;
        }

        player.redirect(targetServer);

        for (CorePlayer staffCorePlayer : NekoCore.getInstance().getOnlinePlayers(IBaseGamer::isStaff))
            staffCorePlayer.sendMessage("§d§lNeko§f§lCloud §8| " + sender.getName() + " §fотправился следить за " + targetPlayer.getDisplayName());

//        new CommonScheduler("spec-commands-" + RandomStringUtils.randomAlphanumeric(8)) {
//            public void run() {
//                targetServer.dispatchCommand("gmsp " + sender.getName());
//                targetServer.dispatchCommand("tp " + sender.getName() + " " + targetPlayer.getName());
//            }
//        }.runLater(1L, TimeUnit.SECONDS);
    }
}