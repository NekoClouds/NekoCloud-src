package me.nekocloud.punishment.core.command;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author xwhilds
 */
public final class InfoCommand extends CommandExecutor {

	public InfoCommand() {
		super("baninfo");

		setGroup(Group.JUNIOR);
	}

	@Override
	protected void execute(final CommandSender sender, @NotNull final String[] args) {

	}
}
