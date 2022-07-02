package me.nekocloud.core.api.connection.player;

import me.nekocloud.core.api.connection.server.Bukkit;

public interface IOfflineData {
	Bukkit getLastServer();

	long getLastOnline();

	String getLastServerName();

}
