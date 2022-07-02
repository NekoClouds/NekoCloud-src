package me.nekocloud.core.common.auth;

import me.nekocloud.core.common.auth.data.AuthData;
import me.nekocloud.core.common.auth.manager.CoreAuthManager;
import me.nekocloud.core.common.auth.sql.CoreAuthSql;
import me.nekocloud.core.io.packet.PacketProtocol;

public class CoreAuth {

	private static CoreAuthManager authManager;
	private static CoreAuthSql authSql;
	private static AuthData authData;

	public static CoreAuthManager getAuthManager() {
		if (authManager == null) {
            authManager = new CoreAuthManager();
        }

        return authManager;
	}

	public static CoreAuthSql getAuthSql() {
		if (authSql == null) {
			authSql = new CoreAuthSql();
		}

		return authSql;
	}

	public static AuthData getAuthData() {
		if (authData == null) {
			authData = new AuthData();
		}

		return authData;
	}

	public CoreAuth() {
		registerPackets();
		authSql = new CoreAuthSql();
	}

	void registerPackets() {
		PacketProtocol.BUNGEE.getMapper().registerPacket(17, AuthData.class);
	}


}
