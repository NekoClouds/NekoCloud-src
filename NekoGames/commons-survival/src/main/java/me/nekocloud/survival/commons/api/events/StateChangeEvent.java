package me.nekocloud.survival.commons.api.events;

import lombok.Getter;
import me.nekocloud.survival.commons.api.User;

@Getter
public abstract class StateChangeEvent extends UserEvent {

    private final boolean result; //результат выполнения

    protected StateChangeEvent(User user, boolean result) {
        super(user);
        this.result = result;
    }

}
