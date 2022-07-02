package me.nekocloud.survival.commons.api.events;

import me.nekocloud.survival.commons.api.User;

public class UserChangeFlyStatusEvent extends StateChangeEvent {

    public UserChangeFlyStatusEvent(User user, boolean fly) {
        super(user, fly);
    }
}
