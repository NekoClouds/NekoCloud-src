package me.nekocloud.market.command;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.market.Market;

public final class ReloadConfigCommand implements CommandInterface {

    private final Market market;

    public ReloadConfigCommand(Market market) {
        this.market = market;

        SpigotCommand command = COMMANDS_API.register("shopreload", this);
        command.setGroup(Group.ADMIN);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        gamerEntity.sendMessage("§d§lMARKET §8| §fКонфиг успешно перезагружен!");
        market.reloadConfig();
    }
}
