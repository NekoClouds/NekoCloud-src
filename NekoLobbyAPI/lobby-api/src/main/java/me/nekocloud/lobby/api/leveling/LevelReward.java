package me.nekocloud.lobby.api.leveling;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;

public abstract class LevelReward {

    /**
     * выдать награду
     * @param gamer - кому выдать
     */
    public abstract void giveReward(BukkitGamer gamer);

    /**
     * получить описание иконки
     * @param language - язык для которого получить
     * @return - описание
     */
    public abstract String getLore(Language language);

    /**
     * чем выше приоритет, тем выше в списке
     */
    public int getPriority() {
        return -1;
    }
}
