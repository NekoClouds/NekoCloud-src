package me.nekocloud.base.gamer;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.sections.Section;
import me.nekocloud.base.skin.Skin;

import java.util.Map;

public interface IBaseGamer {
    int getPlayerID();

    String getName();
    void setName(String name);

    /**
     * Получить ник игрока с префом группы
     */
    String getDisplayName();
    Skin getSkin();

    /**
     * Получить группу игрока
     */
    Group getGroup();
    void setGroup(Group group);
    void setGroup(Group group, boolean mysql);

    String getPrefix();
    void setPrefix(String prefix);

    void addData(String name, Object data);
    <T> T getData(String name);
    void clearData(String name);
    Map<String, Object> getData();

    int getExp();
    int getExpNextLevel();
    int getLevelNetwork();

    /**
     * онлайн или оффлайн игрок
     * @return онлайн или нет
     */
    boolean isOnline();

    /**
     * удалить из памяти
     */
    void remove();

    Map<String, Section> getSections();
    default <T extends Section> T getSection(Class<T> sectionClass) {
        return (T) getSections().get(sectionClass.getSimpleName());
    }

    boolean isDeveloper();
    boolean isOwner();

    boolean isDonater();
    boolean isStaff();
    boolean isTester();

    boolean isDefault();
    boolean isHegent();
    boolean isAkio();
    boolean isTrival();
    boolean isAxside();
    boolean isNeko();
    boolean isTikTok();
    boolean isYouTube();
    boolean isBuilder();
    boolean isJunior();
    boolean isModerator();
    boolean isStModerator();
    boolean isAdmin();


}
