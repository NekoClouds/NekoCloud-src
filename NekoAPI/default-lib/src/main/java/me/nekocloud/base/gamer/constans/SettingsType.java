package me.nekocloud.base.gamer.constans;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
public enum SettingsType {
    HIDER(1), //вырубить нахуй этих игроков
    CHAT(2), //не хочу видеть, что эти дауны поносят
    FLY(3), //не хочу летать, я пидор
    BLOOD(4), //че за ебаное дерьмо
    MUSIC(5), //ебаная музыка, в рот ее
    BOARD(6), //хуев скорборд, нахуя он нужен?
    HUB_GLOWING(7), //быть с подсветкой в хабе
    FRIENDS_REQUEST(8), //запросы в друзья
    PARTY_REQUEST(9),
    PRIVATE_MESSAGE(10), //личные сообщения
    GUILD_REQUEST(11), //запросы в парашу
    DONATE_CHAT(12), //чат даунов//чат челов, которые помогают серву делом, а не еблом
    TEAM_GLOWING(13), //будут ли подсвечиваться тиммейты или нет

    VK_LEAK(14), // могут ли видеть вк игроки
    VK_BOT_ANNOUNCE(15), // уведомления о входе в игру в вк

    AUTO_MESSAGE_ANNOUNCE(16), // Аннонсер
    VANISHED(17), // В ванише ли стафф игрок

    DISCORD_LEAK(18), // могут ли видеть дс игроки
    DISCORD_BOT_ANNOUNCE(19), // уведомления о входе в игру в дс

    GUI_SOUNDS(20), // Звуки кликов в гуи ибаном
    ;

    private static final Int2ObjectMap<SettingsType> SETTINGS = new Int2ObjectOpenHashMap<>();

    public static final int LANGUAGE_KEY = 0; //айди который в БД

    private final int key;

    @Nullable
    public static SettingsType getSettingType(int key) {
        return SETTINGS.get(key);
    }

    static {
        for (val settingsType : values()) {
            SETTINGS.put(settingsType.key, settingsType);
        }
    }

}
