package me.nekocloud.skyblock.api.island.member;

import me.nekocloud.api.depend.BaseUser;
import me.nekocloud.skyblock.api.island.Island;

import java.util.Date;

public interface IslandMember extends BaseUser {

    /**
     * его роль на острова
     */
    MemberType getType();

    /**
     * задать новую роль на сервере
     * @param memberType - какая роль
     */
    void setMemberType(MemberType memberType);

    /**
     * когда он вступил на остров
     * @return - дата
     */
    Date getDate();

    /**
     * онлайн или нет
     * @return - узнать онлайн этот мембер или нет
     */
    boolean isOnline();

    /**
     * к какому острову он принадлежит
     * @return - остров
     */
    Island getIsland();

    /**
     * удалить с острова его
     */
    void remove();
}
