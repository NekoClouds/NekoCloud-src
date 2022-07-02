package me.nekocloud.api;

import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

public interface CoreAPI {

    /**
     * Получить название этого сервера
     * @return название данного сервера
     */
    String getServerName();

    void sendToServer(Player player, String server);

	void sendToServer(BukkitGamer gamer, String regex);

	void executeCommand(BukkitGamer gamer, String command);

	void sendToHub(Player player);

	void addOnlineTask(String regex);

	/**
     * получить онлайн сервера или списка серверов
     * @param namePattern - имя сервера или регикс
     * @return - онлайн
     */
    int getOnline(String namePattern); //@hub or hub-1 or *

    int getGlobalOnline();
}
