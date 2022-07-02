package me.nekocloud.survival.commons.api.events;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;

public class UserCreateWarpEvent extends UserWarpEvent {

    public UserCreateWarpEvent(User user, Warp warp) {
        super(user, warp);
    }
}
