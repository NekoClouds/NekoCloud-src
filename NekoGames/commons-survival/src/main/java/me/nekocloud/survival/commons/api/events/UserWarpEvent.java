package me.nekocloud.survival.commons.api.events;

import lombok.Getter;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;

public abstract class UserWarpEvent extends UserEvent {

    @Getter
    private final Warp warp;

    protected UserWarpEvent(User user, Warp warp) {
        super(user);
        this.warp = warp;
    }
}
