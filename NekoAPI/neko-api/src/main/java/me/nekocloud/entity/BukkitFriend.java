package me.nekocloud.entity;

import lombok.RequiredArgsConstructor;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.friends.Friend;

@RequiredArgsConstructor
public final class BukkitFriend implements Friend {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    public final int friendId;

    @Override
    public int getPlayerId() {
        return friendId;
    }

    @Override
    public boolean isOnline() {
        return GAMER_MANAGER.getGamer(friendId) != null;
    }

    @Override
    public <T extends IBaseGamer> T getGamer() {
        return (T) GAMER_MANAGER.getOrCreate(friendId);
    }
}
