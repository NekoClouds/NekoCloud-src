package me.nekocloud.auth.commands;

import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.objects.AuthPlayer;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

public class LicenseCommand extends AuthCommand {
	public LicenseCommand(final BungeeAuth plugin) {
		super(plugin, "license", "лицения", "лицуха", "дшсутыу");
		setOnlyPlayers(true);
	}

	@Override
	public void execute(final AuthPlayer authUser, final BungeeGamer gamer, final String[] args) {
		gamer.sendMessage("лицензия не доделана иди нахуй ");
	}

	@Override
	public Iterable<String> tabComplete(final BungeeEntity entity, final String[] args) {
		return null;
	}
}
