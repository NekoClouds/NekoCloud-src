package me.nekocloud.core.api.connection.server;

import me.nekocloud.core.api.connection.Connection;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.io.info.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface Bukkit extends Connection {

	/**
	 * Получить коллекцию всех игроков
	 * которые находятся на этом сервере
	 *
	 * @return коллекция объектов игроков
	 */
	Collection<CorePlayer> getOnlinePlayers();

	/**
	 * Получить ип этого сервера
	 *
	 * @return InetSocketAddress
	 */
	InetSocketAddress getAddress();

	/**
	 * Получить порт этого сервера
	 *
	 * @return int порт
 	 */
	int getPort();

	/**
	 * Выполнить команду на этом сервере
	 *
	 * @param command команда
	 */
	void sendCommand(final String command);

	/**
	 * Отправить этот сервер на перезагрузку
	 */
	void restart();

	/**
	 * Перезагрузить локализацию на этом сервере
	 */
	void langReload();

	/**
	 * Получить онлайн сервера
	 *
	 * @return int онлайн
	 */
	int getOnline();

	/**
	 * Получить полезную инфу сервера
	 *
	 * @return объект и инфой сервера
	 */
	ServerInfo getServerInfo();
}
