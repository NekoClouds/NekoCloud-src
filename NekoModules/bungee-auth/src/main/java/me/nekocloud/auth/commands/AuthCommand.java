package me.nekocloud.auth.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.manager.AuthManager;
import me.nekocloud.auth.objects.AuthPlayer;
import me.nekocloud.auth.sql.AuthSql;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.commands.ProxyCommand;

import javax.annotation.Nullable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AuthCommand extends ProxyCommand<BungeeAuth> {

	@Setter boolean onlyAuthorized = true;

	protected AuthManager manager;
	protected AuthSql authSql;

	public AuthCommand(final BungeeAuth plugin, final String name, final String... aliases) {
		super(plugin, name, aliases);

		this.manager = plugin.getAuthManager();
		this.authSql = plugin.getAuthSql();
	}

	@Override
	public void execute(final BungeeEntity entity, final String[] args) {
		val gamer = (BungeeGamer) entity;

		@Nullable val authUser = manager.loadOrCreate(gamer.getPlayerID());
		if (authUser == null && onlyAuthorized) {
			gamer.sendMessageLocale("AUTH_NOT_REGISTERED");
			return;
		}

//		if (!authUser.isAuthorized() && onlyAuthorized) {
//			gamer.sendMessageLocale("AUTH_NOT_AUTHORIZED");
//			return;
//		}

		execute(authUser, gamer, args);
	}

	public abstract void execute(final AuthPlayer authUser, final BungeeGamer gamer, final String[] args);
}
