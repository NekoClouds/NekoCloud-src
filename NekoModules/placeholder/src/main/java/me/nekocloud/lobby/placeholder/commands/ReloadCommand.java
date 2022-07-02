package me.nekocloud.lobby.placeholder.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.lobby.placeholder.PlaceHolder;

public class ReloadCommand implements CommandInterface {

    final PlaceHolder placeHolder;

    public ReloadCommand(PlaceHolder placeHolder) {
        val command = COMMANDS_API.register("placeholder", this);
        command.setGroup(Group.ADMIN);
        this.placeHolder = placeHolder;
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        gamerEntity.sendMessage("§d§lPLACEHOLDER §8| §fКонфиг плагина перезагружен!");
        placeHolder.reloadConfig();
    }
}
