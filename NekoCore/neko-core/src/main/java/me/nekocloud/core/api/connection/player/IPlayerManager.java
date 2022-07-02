package me.nekocloud.core.api.connection.player;

import com.google.common.cache.Cache;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.nekocloud.base.util.map.MultikeyMap;
import me.nekocloud.core.connection.player.PlayerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

public interface IPlayerManager {
	/**
	 * Получить все оффлайн сообщения, которые
	 * еще не отправлены игроку
	 *
	 * @return мапа сообщений
	 */
	Multimap<String, Supplier<String>> getOfflineMessageMap();

	/**
	 * Получить кэш оффлайн игроков
	 *
	 * @return кэш с оффлайн игроками
	 */
	Cache<String, CorePlayer> getOfflineCache();

	/**
	 * Получить мапу игроков коры
	 *
	 * @return мапа с игроками
	 */
	MultikeyMap<CorePlayer> getCorePlayerMap();

	/**
	 * Получить хеш с ид игроков
	 */
	Object2IntMap<String> getCachedIds();

	/**
     * Подключить игрока к кору, добавив в кеш
	 *
     * @param corePlayer - игрок
     */
	void playerConnect(final @NotNull CorePlayer corePlayer);

	/**
     * Отключить игрока от кора, удалив его из кеша
	 *
     * @param corePlayer - игрок
     */
	void playerDisconnect(final @NotNull CorePlayer corePlayer);

	/**
     * Получить кешированного игрока по его нику
	 *
     * @param playerName - ник игрока
     */
	@Nullable
	CorePlayer getPlayer(final @NotNull String playerName);

	/**
     * Получить кешированного игрока по его ид
     *
     * @param playerID - номер игрока
     */
	@Nullable
	CorePlayer getPlayer(final int playerID);

	/**
     * Получить offline данные игрока
     * @param playerName ник игрока
     */
	@Nullable
	CorePlayer getOfflinePlayer(final @NotNull String playerName);

	/**
     * Проверить, есть ли игрок в мапе
     *
     * @param playerID - ид игрока
	 * @return true or false
     */
	boolean isOnline(final int playerID);

	/**
     * Проверить, есть ли игрок в мапе
     *
     * @param playerName - ник игрока
	 * @return true or false
     */
	boolean isOnline(final @NotNull String playerName);

	/**
     * Отправить offline сообщения
     *
     * @param playerName      - оффлайн игрок
     * @param messageSupplier - обработчик сообщения
     */
	void sendOfflineMessage(final @NotNull String playerName, Supplier<String> messageSupplier);

	/**
     * Получить отфлитрованный список онлайн игроков
     * по какому-то условию
     *
     * @param playerResponseHandler - условие фильтрования онлайн игроков
     */
	Collection<CorePlayer> getOnlinePlayers(final @NotNull PlayerManager.PlayerResponseHandler playerResponseHandler);
}
