package me.nekocloud.guilds.core.command;

import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GuildCommand extends CommandExecutor {

	public GuildCommand() {
		super("guild");
	}

	@Override
	protected void execute(CommandSender sender, @NotNull String[] args) {

	}
}
