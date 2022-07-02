package me.nekocloud.core.api.connection.server;

import me.nekocloud.core.api.connection.Connection;
import me.nekocloud.core.api.connection.player.CorePlayer;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface Bungee extends Connection {
	/**
	 * Получить коллекцию всех игроков
	 * которые находятся на этой прокси
	 *
	 * @return коллекция объектов игроков
	 */
	Collection<CorePlayer> getOnlinePlayers();

	/**
	 * Получить адресс текущей прокси
	 *
	 * @return InetSocketAddress
	 */
	InetSocketAddress getAddress();

	/**
	 * Обновить онлайн
	 */
	void ensureOnline();

	/**
	 * Обновить список команд
	 */
	void ensureCommands();

	/**
	 * Получить онлайн текущей прокси
	 * @return int онлайн
	 */
	int getOnline();
}
