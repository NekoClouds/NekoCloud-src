package me.nekocloud.lobby.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.lobby.api.leveling.Leveling;
import me.nekocloud.lobby.api.profile.BoardLobby;
import me.nekocloud.lobby.api.profile.ProfileGui;
import me.nekocloud.lobby.profile.gui.ProfileGuiManagerImpl;
import me.nekocloud.lobby.profile.leveling.LevelingImpl;

@UtilityClass
public class LobbyAPI {

    private GuiManager<ProfileGui> profileGuiManager;
    private Leveling leveling;

    @Setter @Getter
    private BoardLobby boardLobby;

    public GuiManager<ProfileGui> getProfileGuiManager() {
        if (profileGuiManager == null) {
            profileGuiManager = new ProfileGuiManagerImpl();
        }
        return profileGuiManager;
    }

    public Leveling getLeveling() {
        if (leveling == null) {
            leveling = new LevelingImpl();
        }
        return leveling;
    }
}
