package me.nekocloud.friends.core;

import lombok.RequiredArgsConstructor;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.friends.Friend;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;

@RequiredArgsConstructor
public final class CoreFriend implements Friend {

	private final int friendID;

	@Override
	public int getPlayerId() {
		return friendID;
	}

	@Override
	public boolean isOnline() {
		return NekoCore.getInstance().getPlayer(friendID) != null;
	}

	@Override
	public <T extends IBaseGamer> T getGamer() {
		return (T) CorePlayer.getPlayer(friendID);
	}
}
