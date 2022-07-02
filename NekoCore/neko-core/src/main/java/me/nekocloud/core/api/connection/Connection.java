package me.nekocloud.core.api.connection;

import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.packet.DefinedPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Connection {

    ChannelWrapper getChannel();

	/**
	 * Отправить пакет
	 *
	 * @param packet definedPacket
	 */
    void sendPacket(final DefinedPacket packet);

    String getName();

	/**
     * Получить игрока этого сервера/прокси
     * @param playerID айди игрока
	 *
     * @return объект игрока
     */
	CorePlayer getPlayer(final int playerID);

	/**
     * добавить игрока в мультимапу текущего сервера
     *
	 * @param corePlayer объект игрока
     */
	void addPlayer(final CorePlayer corePlayer);

	/**
     * удалить игрока из мультимапы текущего сервера
	 *
     * @param corePlayer объект игрока
     */
	void removePlayer(final CorePlayer corePlayer);

	Collection<CorePlayer> getPlayers(final @NotNull ResponseHandler responseHandler);

	interface ResponseHandler {
        boolean handle(final @NotNull CorePlayer corePlayer);
    }
}
