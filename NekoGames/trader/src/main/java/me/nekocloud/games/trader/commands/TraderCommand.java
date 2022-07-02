package me.nekocloud.games.trader.commands;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.games.trader.gui.TraderGui;

public class TraderCommand implements CommandInterface {

    private static final GuiManager<AbstractGui<?>> GUI_MANAGER = NekoCloud.getGuiManager();

    public TraderCommand() {
        val command = COMMANDS_API.register("trader",
                this, "продавецговна");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        val gamer = (BukkitGamer) gamerEntity;
        if (gamer == null)
            return;

        GUI_MANAGER.getGui(TraderGui.class, gamer.getPlayer()).open();
    }
}
