package me.nekocloud.survival.commons.api.events;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;

public class UserRemoveWarpEvent extends UserWarpEvent {

    public UserRemoveWarpEvent(User user, Warp warp) {
        super(user, warp);
    }
}
