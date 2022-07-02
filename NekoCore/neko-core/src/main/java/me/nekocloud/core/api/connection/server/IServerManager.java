package me.nekocloud.core.api.connection.server;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public interface IServerManager {

	/**
	 * Получить все сервера из мапы
	 */
	Map<String, Bukkit> getBukkitServers();

	/**
	 * Получить все прокси сервера из мапы
	 */
	Map<String, Bungee> getBungeeServers();

	/**
	 * Добавить прокси сервер в мапу
	 * @param bukkit новый объект сервера
	 */
	void addBukkit(final @NotNull Bukkit bukkit);

	/**
	 * Добавить прокси сервер в мапу
	 * @param bungee новый объект прокси сервера
	 */
	void addBungee(final @NotNull Bungee bungee);

	/**
	 * Получить покдлюченный сервер
	 * @param serverName имя сервера
	 * @return объект сервера
	 */
	Bukkit getBukkit(final @NotNull String serverName);

	/**
	 * Получить покдлюченный прокси сервер
	 * @param serverName имя прокси сервера
	 * @return объект прокси сервера
	 */
	Bungee getBungee(final @NotNull String serverName);

	/**
	 * Удалить сервер из мапы
	 * @param serverName имя сервера
	 */
	void removeBukkit(final @NotNull String serverName);

	/**
	 * Удалить прокси сервер из мапы
	 * @param serverName имя прокси сервера
	 */
	void removeBungee(final @NotNull String serverName);

	 /**
     * Получить сумму онлайна нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
	int getOnlineByServerPrefix(final @NotNull String serverPrefix);

	int getOnlineByProxyPrefix(@NotNull String serverPrefix);

	/**
     * Получить коллекцию нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
	Collection<Bukkit> getServersByPrefix(final @NotNull String serverPrefix);

	/**
     * Получить коллекцию нескольких прокси серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
	Collection<Bungee> getProxiesByPrefix(final @NotNull String serverPrefix);
}
