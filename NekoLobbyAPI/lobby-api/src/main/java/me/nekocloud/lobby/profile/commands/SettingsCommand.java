package me.nekocloud.lobby.profile.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.lobby.profile.gui.guis.SettingsPage;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.api.profile.ProfileGui;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandInterface {

    private final GuiManager<ProfileGui> guiManager = LobbyAPI.getProfileGuiManager();

    public SettingsCommand() {
        SpigotCommand command = COMMANDS_API.register("settings", this, "настройки");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        val gamer = (BukkitGamer) gamerEntity;
        val player = gamer.getPlayer();

        val page = guiManager.getGui(SettingsPage.class, player);
        if (page == null) {
            return;
        }

        page.open();
    }
}
