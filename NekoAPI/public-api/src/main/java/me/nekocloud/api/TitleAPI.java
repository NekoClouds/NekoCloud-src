package me.nekocloud.api;

import org.bukkit.entity.Player;

public interface TitleAPI {

    /**
     * отправить title игроку
     * @param player - кому слать
     * @param title - большое сообщение
     */
    void sendTitle(Player player, String title);

    /**
     * отправить title и subtitle игроку
     * @param player - кому слать
     * @param title - большое сообщение
     * @param subTitle - маленькое сообщение
     */
    void sendTitle(Player player, String title, String subTitle);

    /**
     * отправить title и subtitle игроку
     * @param player - кому слать
     * @param title - большое сообщение
     * @param subTitle - маленькое сообщение
     * @param fadeInTime - как быстро появляется
     * @param stayTime - сколько висеть будет
     * @param fadeOutTime - как быстро пропадать
     */
    void sendTitle(Player player, String title, String subTitle, long fadeInTime, long stayTime, long fadeOutTime);

    void sendTitleAll(String title);

    void sendTitleAll(String title, String subTitle);

    void sendTitleAll(String title, String subTitle, long fadeInTime, long stayTime, long fadeOutTime);
}
