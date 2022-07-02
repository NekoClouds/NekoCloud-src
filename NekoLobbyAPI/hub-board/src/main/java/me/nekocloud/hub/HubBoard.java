package me.nekocloud.hub;

import me.nekocloud.hub.command.HorseCommand;
import me.nekocloud.hub.listeners.HorseListener;
import me.nekocloud.lobby.api.LobbyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class HubBoard extends JavaPlugin {

    @Override
    public void onEnable() {
        LobbyAPI.setBoardLobby(new HBoard());

        new HorseListener(this);
        new HorseCommand(this);

        /*
        if (ChristmasThings.supports()) {
            this.christmasThings = new ChristmasThings(this);
            this.christmasThings.register();
        }
        */
    }


}