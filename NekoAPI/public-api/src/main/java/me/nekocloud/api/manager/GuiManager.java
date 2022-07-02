package me.nekocloud.api.manager;

import org.bukkit.entity.Player;

import java.util.Map;

public interface GuiManager<G> {

    /**
     * создать гуи
     * @param clazz класс созданного гуи
     */
    void createGui(final Class<? extends G> clazz);

    /**
     * удалить всем это гуи и отключить его
     * @param clazz класс гуи, что удаляется
     */
    void removeGui(final Class<? extends G> clazz);

    /**
     * получить гуи у игрока или создатиь новое
     * @param clazz класс гуи
     * @param player для кого получить гуи
     * @return объект гуи
     */
    <T extends G> T getGui(final Class<T> clazz, final Player player);

    /**
     * при выходе игрока удалить ему все гуи в памяти
     * @param player игрок которому удалять
     */
    void removeALL(final Player player);

    Map<String, Map<String, G>> getPlayerGuis();
}
