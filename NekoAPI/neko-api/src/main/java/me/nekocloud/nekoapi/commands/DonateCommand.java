package me.nekocloud.nekoapi.commands;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import me.nekocloud.nekoapi.guis.basic.DonateGui;

public final class DonateCommand implements CommandInterface {

    private final GuiDefaultContainer container;

    public DonateCommand(GuiDefaultContainer container) {
        this.container = container;
        final SpigotCommand command = COMMANDS_API.register("donate", this,
                "donat", "донат", "дон");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(final GamerEntity gamerEntity, final String command, String[] args) {
        container.openGui(DonateGui.class, ((BukkitGamer)gamerEntity).getPlayer());
    }

}
