package me.nekocloud.auth;

import me.nekocloud.auth.commands.*;
import me.nekocloud.auth.core.AuthData;
import me.nekocloud.auth.listeners.AuthListener;
import me.nekocloud.auth.manager.AuthManager;
import me.nekocloud.auth.sql.AuthSql;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.PacketProtocol;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BungeeAuth extends Plugin { // ГОВНОКОД МНЕ ПОХУЙ

	private static AuthManager authManager;
	private static AuthSql authSql;

	@Override
	public void onEnable() {
		registerPackets();
		registerCommands();
		registerListeners();

		authSql = new AuthSql();
	}

	@Override
	public void onDisable() {
		PacketProtocol.BUNGEE.getMapper().unregisterPacket(17);
	}

	void registerListeners() {
		new AuthListener(this);
	}

	void registerPackets() {
		PacketProtocol.BUNGEE.getMapper().registerPacket(17, AuthData.class);
	}

	void registerCommands() {
		new RegisterCommand(this);
		new LoginCommand(this);
		new LogoutCommand(this);
		new LicenseCommand(this);
		new ChangePasswordCommand(this);

		new AdminAuthCommand(this);
	}

	public void sendPacket(final @NotNull DefinedPacket packet) {
		CoreConnector.getInstance().sendPacket(packet);
	}

	public AuthManager getAuthManager() {
		if (authManager == null) {
            authManager = new AuthManager();
        }

        return authManager;
	}

	public AuthSql getAuthSql() {
		if (authSql == null) {
			authSql = new AuthSql();
		}

		return authSql;
	}
}
