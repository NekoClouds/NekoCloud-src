package me.nekocloud.survival.commons.api.events;

import me.nekocloud.survival.commons.api.User;
import org.bukkit.GameMode;

public class UserChangeGamemodeEvent extends UserEvent {

    private GameMode gameMode;

    public UserChangeGamemodeEvent(User user, GameMode gameMode) {
        super(user);
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
