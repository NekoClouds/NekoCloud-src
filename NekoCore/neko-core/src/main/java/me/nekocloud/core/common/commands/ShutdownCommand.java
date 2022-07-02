package me.nekocloud.core.common.commands;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class ShutdownCommand extends CommandExecutor {

    public ShutdownCommand() {
        super("shutdown","end");
        setGroup(Group.OWNER);
    }

    @Override
    protected void execute(
            final CommandSender sender,
            final String @NotNull[] args
    ) {
        if (sender instanceof CorePlayer) {
            sender.sendMessage("§cДанная команда доступна только из консоли!");
            return;
        }

        CoreCommand.alert("Остановка кора...");
        NekoCore.getInstance().shutdown();
    }
}
