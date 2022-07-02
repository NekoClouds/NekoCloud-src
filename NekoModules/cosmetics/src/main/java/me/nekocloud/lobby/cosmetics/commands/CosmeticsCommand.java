package me.nekocloud.lobby.cosmetics.commands;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.manager.GuiManager;
import org.bukkit.entity.Player;

public abstract class CosmeticsCommand implements CommandInterface {

    private final GuiManager<AbstractGui<?>> profileGuiManager = NekoCloud.getGuiManager();

    public CosmeticsCommand(String name, String ... aliases) {
        val commandSource = COMMANDS_API.register(name,this, aliases);
        commandSource.setOnlyPlayers(true);
    }

    public void openGui(Class<? extends AbstractGui<?>> guiClass, Player player) {
        profileGuiManager.getGui(guiClass, player).open();
    }
}

