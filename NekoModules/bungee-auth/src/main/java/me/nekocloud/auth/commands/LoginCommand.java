package me.nekocloud.auth.commands;

import lombok.val;
import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.core.AuthAction;
import me.nekocloud.auth.core.AuthData;
import me.nekocloud.auth.objects.AuthPlayer;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

public class LoginCommand extends AuthCommand {

	public LoginCommand(final BungeeAuth plugin) {
		super(plugin, "login", "l", "дщгин", "логин");
		setOnlyPlayers(true);
	}

	@Override
	public void execute(final AuthPlayer authUser, final BungeeGamer gamer, String[] args) {
        if (authUser.isAuthorized() || manager.has2FASession(gamer.getPlayerID())) {
            gamer.sendMessageLocale("AUTH_ALREADY_LOGGED");
            return;
        }

        if (args.length < 1) {
            gamer.sendMessageLocale("AUTH_LOGIN_USAGE");
            return;
        }

        val password = args[0];
        if (manager.equalsPassword(authUser.getPlayerPassword(), password)) {
            gamer.sendMessageLocale("AUTH_LOGIN_WRONG_PASSWORD");
            return;
        }

		plugin.sendPacket(new AuthData(gamer.getPlayerID(), AuthAction.WAIT_2FA_CODE));
	}

	@Override
	public Iterable<String> tabComplete(BungeeEntity entity, String[] args) {
		return null;
	}
}
