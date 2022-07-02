package me.nekocloud.lobby.profile.gui.guis;

import me.nekocloud.lobby.api.profile.ProfileGui;
import org.bukkit.entity.Player;

public class ProfileStatsGui extends ProfileGui {

    protected ProfileStatsGui(Player player) {
        super(player, "STATS_GUI_TITLE");
    }

    @Override
    protected void setItems() {
        setBackItem();
        setGlassItems();

    }
}
