package me.nekocloud.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BorderAPI {

    /**
     * Отправить красный экран
     * @param player - кому слать
     */
    void sendRedScreen(Player player);

    /**
     * Отправить красный экран
     * @param player - кому слать
     * @param time - продолжительность (по дефолту 5 тиков)
     */
    void sendRedScreen(Player player, long time);

    /**
     * Отправить красный экран
     * @param player - кому слать
     * @param time - продолжительность (по дефолту 5 тиков)
     * @param percentage - как сильно краснет надо
     */
    void sendRedScreen(Player player, long time, int percentage);

    /**
     * Отправить борд из которого игрок не может выйти
     * @param player - кому слать
     * @param location - локация центра
     * @param size - размерность от цента в блоках
     */
    void sendBoard(Player player, Location location, double size);

    /**
     * удалить борд (если есть)
     * @param player - кому удалить
     */
    void removeBoard(Player player);
}
