package me.nekocloud.base.gamer;

import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public interface OnlineGamer extends IBaseGamer {

    void sendMessage(String message);

    void sendTitle(String title, String subTitle);
    void sendTitle(String title, String subTitle, long fadeInTime, long stayTime, long fadeOutTime);
    void sendActionBar(String msg);
    void sendActionBarLocale(String key, Object... replaced);

    /**
     * Получить версию игрока
     * @return версия с которой игрок
     */
    Version getVersion(); //версия с которой играет игрок

    /**
     * Получить ип игрока
     * @return ip
     */
    InetAddress getIp();

    /**
     * Получить игровой язык игрока
     * @return язык
     */
    Language getLanguage();

    /**
     * Получить настройку игрока
     * @param type тип
     * @return true/false
     */
    boolean getSetting(SettingsType type);

    /**
     * Изменить настройку игрока
     * @param type тип
     * @param key true/false
     */
    void setSetting(SettingsType type, boolean key);

	// TODO: Дописать, очень крутая штука.
	@SuppressWarnings("all")
	<T> T getDatabaseValue(@NotNull String table, @NotNull String column);
}
