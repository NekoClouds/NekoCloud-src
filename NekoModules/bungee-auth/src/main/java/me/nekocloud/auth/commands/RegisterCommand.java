package me.nekocloud.auth.commands;

import lombok.val;
import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.objects.AuthPlayer;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

public class RegisterCommand extends AuthCommand {

	public RegisterCommand(final BungeeAuth plugin) {
		super(plugin, "register", "reg", "регистрация");
		setOnlyPlayers(true);
		setOnlyAuthorized(false);
	}

	@Override
	public void execute(final AuthPlayer authUser, final BungeeGamer gamer, final String[] args) {
        val playerID = gamer.getPlayerID();
        if (manager.hasPlayerAccount(playerID) && authUser != null) {
            gamer.sendMessageLocale("AUTH_ALREADY_REGISTERED");
            return;
        }

        if (args.length < 2) {
            gamer.sendMessageLocale("AUTH_REGISTER_USAGE");
            return;
        }

        val currentPassword = args[0];
        val confirmPassword = args[1];

        if (currentPassword.length() < 3) {
            gamer.sendMessageLocale("AUTH_MIN_PASSWORD_LENGTH");
            return;
        } else if (currentPassword.length() > 16) {
            gamer.sendMessageLocale("AUTH_MAX_PASSWORD_LENGTH");
            return;
        }

        if (!currentPassword.equals(confirmPassword)) {
            gamer.sendMessageLocale("AUTH_PASSWORD_MISMATCH");
            return;
        }

        gamer.sendMessageLocale("AUTH_ACCOUNT_NOT_LINKED_VK");
        manager.registerPlayer(gamer.getPlayerID(), confirmPassword, gamer.getIp(), plugin);
	}

	@Override
	public Iterable<String> tabComplete(BungeeEntity entity, String[] args) {
		return null;
	}
}
