package me.nekocloud.core.resumer;

import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RejoinCommand extends CommandExecutor {

    public RejoinCommand() {
        super("rejoin", "перезаход");
    }

    @Override
    protected void execute(CommandSender sender, @NotNull String[] args) {

    }
}
