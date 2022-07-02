package me.nekocloud.lobby.profile.gui;

import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.api.profile.ProfileGui;
import me.nekocloud.lobby.profile.commands.ProfileCommand;
import me.nekocloud.lobby.profile.commands.SettingsCommand;
import me.nekocloud.lobby.profile.gui.guis.LangPage;
import me.nekocloud.lobby.profile.gui.guis.ProfileMainPage;
import me.nekocloud.lobby.profile.gui.guis.RewardLevelGui;
import me.nekocloud.lobby.profile.gui.guis.SettingsPage;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileGuiListener extends DListener<Lobby> {

    private final GuiManager<ProfileGui> manager = LobbyAPI.getProfileGuiManager();

    public ProfileGuiListener(Lobby lobby) {
        super(lobby);

        new ProfileCommand();
        new SettingsCommand();

        //создать все гуи
        manager.createGui(ProfileMainPage.class);
        manager.createGui(LangPage.class);
        manager.createGui(SettingsPage.class);
        manager.createGui(RewardLevelGui.class);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        manager.removeALL(e.getPlayer());
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }
        manager.removeALL(player);
    }
}
