package me.nekocloud.lobby.commands;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.lobby.Lobby;

public class ReloadConfigCommand implements CommandInterface {

    private final Lobby lobby;

    public ReloadConfigCommand(Lobby lobby) {
        this.lobby = lobby;

        SpigotCommand spigotCommand = COMMANDS_API.register("lobbyreload", this);
        spigotCommand.setGroup(Group.ADMIN);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        lobby.reloadConfig();
        gamerEntity.sendMessage("§d§lЛОББИ §8| §fКонфиг перезагружен!");
    }
}
