package me.nekocloud.base.gamer.friends;

import me.nekocloud.base.gamer.IBaseGamer;

public interface Friend {

    int getPlayerId();

    boolean isOnline();

    <T extends IBaseGamer> T getGamer();

}
