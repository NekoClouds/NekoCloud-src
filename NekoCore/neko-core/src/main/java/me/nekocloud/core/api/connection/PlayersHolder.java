package me.nekocloud.core.api.connection;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.nekocloud.core.api.connection.player.CorePlayer;

public interface PlayersHolder {

    CorePlayer getPlayer(int id);

    Int2ObjectMap<CorePlayer> getPlayers();

    int getOnline();
}
