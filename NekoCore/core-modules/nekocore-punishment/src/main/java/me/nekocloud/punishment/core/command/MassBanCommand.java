package me.nekocloud.punishment.core.command;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MassBanCommand extends CommandExecutor {

	public MassBanCommand() {
		super("massban");

		setGroup(Group.ADMIN);
		setOnlyAuthorized(true);
		setOnlyPlayers(true);
	}

	@Override
	protected void execute(CommandSender sender, @NotNull String[] args) {

	}
}
