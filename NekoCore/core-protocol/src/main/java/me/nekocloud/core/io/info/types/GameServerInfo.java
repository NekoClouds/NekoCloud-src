package me.nekocloud.core.io.info.types;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.base.game.GameState;
import me.nekocloud.core.io.info.ServerInfoType;

public class GameServerInfo extends DefaultServerInfo {

    @Getter @Setter
    private GameState state = GameState.WAITING;

    @Override
    public ServerInfoType getType() {
        return ServerInfoType.GAME;
    }
}
