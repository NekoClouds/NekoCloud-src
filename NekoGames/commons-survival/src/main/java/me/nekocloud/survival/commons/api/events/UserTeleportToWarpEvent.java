package me.nekocloud.survival.commons.api.events;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;

public class UserTeleportToWarpEvent extends UserWarpEvent {

    public UserTeleportToWarpEvent(User user, Warp warp) {
        super(user, warp);
    }
}
