package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.nekoapi.guis.basic.HelpGui;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;

public final class HelpCommand implements CommandInterface {

    private final GuiDefaultContainer container;

    public HelpCommand(GuiDefaultContainer guiDefaultContainer) {
        this.container = guiDefaultContainer;
        val command = COMMANDS_API.register("help", this, "помощь");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        val gamer = (BukkitGamer) gamerEntity;
        val player = gamer.getPlayer();

        container.openGui(HelpGui.class, player);
    }
}
