package me.nekocloud.skyblock.api.entity;

import me.nekocloud.nekoapi.achievements.achievement.AchievementPlayer;

public interface SkyGamer {

    /**
     * ставить ли бордер этому игроку
     * @return - ставить или нет
     */
    boolean isBorder();

    /**
     * поставить бордер
     * @param flag - ставить или нет
     */

    void setBorder(boolean flag);

    /**
     * получить имя игрока
     * @return - имя
     */
    String getName();

    /**
     * получить объект хранящий ачивки
     * @return - объект игрока
     */
    AchievementPlayer getAchievementPlayer();

    /**
     * удалить из памяти
     */
    void remove();
}
