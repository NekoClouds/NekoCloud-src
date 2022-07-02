package me.nekocloud.api.player;

import org.bukkit.plugin.java.JavaPlugin;

public interface Spigot extends GamerEntity { //сам сервер спигот

    /**
     * плагин овнер некоапи
     * @return - замена getInstance();
     */
    JavaPlugin getMainPlugin();

    /**
     * всем игрокам проспамить
     * @param message - сообщение
     */
    void broadcast(String message);

    void broadcastLocale(String key, Object... objects);
}
