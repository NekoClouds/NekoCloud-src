package me.nekocloud.auth.commands;

import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.objects.AuthPlayer;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

public class LogoutCommand extends AuthCommand {

	public LogoutCommand(final BungeeAuth plugin) {
		super(plugin, "logout");
		setOnlyPlayers(true);
	}

	@Override
	public void execute(final AuthPlayer authUser, final BungeeGamer gamer, final String[] args) {
		authUser.logout();
	}

	@Override
	public Iterable<String> tabComplete(final BungeeEntity entity, final String[] args) {
		return null;
	}
}
