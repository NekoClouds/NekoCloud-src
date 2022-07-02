package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.friends.Friend;
import me.nekocloud.base.gamer.friends.FriendAction;

@Getter
public class GamerFriendEvent extends GamerEvent {

    private final Friend friend;
    private final FriendAction action;

    public GamerFriendEvent(BukkitGamer gamer, Friend friend, FriendAction action) {
        super(gamer);
        this.friend = friend;
        this.action = action;
    }
}
