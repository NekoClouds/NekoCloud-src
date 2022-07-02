package me.nekocloud.core.api.connection;

import com.google.common.collect.BiMap;
import me.nekocloud.base.gamer.constans.Group;
import org.jetbrains.annotations.NotNull;

public interface INetworkManager {

	BiMap<Integer, String> getPlayerIdsMap();

	String getPlayerName(int playerID);

	int getPlayerID(String playerName);

	boolean hasIdentifier(String name);

	Group getPlayerGroup(int playerId);

	Group getPlayerGroup(final @NotNull String playerName);
}
