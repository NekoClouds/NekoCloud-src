package me.nekocloud.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ActionBarAPI {

    /**
     * отправить бар игроку
     * @param player - кому слать
     * @param message - какое сообщение
     */
    void sendBar(Player player, String message);

    /**
     * Отправить сообщение игрокам
     * @param players - кому слать
     * @param message - какое сообщение
     */
    void sendBar(Collection<Player> players, String message);

    /**
     * Отправить анимированное оообщение
     * @param player - кому слать
     * @param message - какое сообщение
     */
    void sendAnimatedBar(Player player, String message);
}
