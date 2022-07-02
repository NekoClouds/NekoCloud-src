package me.nekocloud.api.depend;

import me.nekocloud.base.gamer.IBaseGamer;
import org.bukkit.entity.Player;

/**
 * для какие-то других обектов игроков(например для гаджетов)
 */
public interface BaseUser {

    int getPlayerID();

    Player getPlayer();

    String getName();

    IBaseGamer getGamer();
}
