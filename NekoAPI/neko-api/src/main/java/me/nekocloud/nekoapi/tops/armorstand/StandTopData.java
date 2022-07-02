package me.nekocloud.nekoapi.tops.armorstand;

import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import org.bukkit.inventory.ItemStack;

public interface StandTopData {

    /**
     * для какого топа эта инфа
     * @return - какого топа
     */
    Top getTop();

    /**
     * ид игрока топера
     * @return - айди игрока
     */
    int getPlayerID();

    /**
     * позиция в топе
     * @return - позиция
     */
    int getPosition();

    /***
     * вызывается чтобы получить голову игрока из этого топа
     * @return - голова
     */
    ItemStack getHead();

    /**
     * вызывается чтобы получить последнюю строчку со статой
     * @param language - язык
     * @return - ласт строчка
     */
    String getLastString(Language language);

    /**
     * получить имя топера
     * @return - имя
     */
    String getDisplayName();

    IBaseGamer getGamer();
    void setGamer(IBaseGamer gamer);
}
