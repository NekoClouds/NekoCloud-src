package me.nekocloud.punishment.core.command;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author xwhilds
 */
public final class HistoryCommand extends CommandExecutor {

	public HistoryCommand() {
		super("history", "история");

		setGroup(Group.MODERATOR);
	}

	@Override
	protected void execute(CommandSender sender, @NotNull String[] args) {

	}
}
