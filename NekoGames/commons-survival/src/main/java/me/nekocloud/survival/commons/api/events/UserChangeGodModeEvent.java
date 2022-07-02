package me.nekocloud.survival.commons.api.events;

import me.nekocloud.survival.commons.api.User;

public class UserChangeGodModeEvent extends StateChangeEvent {

    public UserChangeGodModeEvent(User user, boolean god) {
        super(user, god);
    }
}
