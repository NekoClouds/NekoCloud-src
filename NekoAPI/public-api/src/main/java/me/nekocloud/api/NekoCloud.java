package me.nekocloud.api;

import me.nekocloud.api.command.CommandsAPI;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.exeption.ApiNotLoadedException;
import me.nekocloud.api.gui.DefaultGui;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.usableitem.UsableAPI;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.game.SubType;

public final class NekoCloud {

    private static GamerManager gamerManagerAPI;
    private static UsableAPI usableAPI;
    private static HologramAPI hologramAPI;
    private static ScoreBoardAPI scoreBoardAPI;
    private static EntityAPI entityAPI;
    private static CommandsAPI commandsAPI;
    private static InventoryAPI inventoryAPI;
    private static TitleAPI titleAPI;
    private static BorderAPI borderAPI;
    private static ActionBarAPI actionBarAPI;
    private static SoundAPI soundAPI;
    private static JSONMessageAPI jsonMessageAPI;
    private static ParticleAPI particleAPI;
    private static CoreAPI coreAPI;
    private static GuiManager<DefaultGui<?>> guiManager;

    public static boolean isLobby() {
        return SubType.current == SubType.LOBBY
                && GameType.current != GameType.UNKNOWN;
    }

    public static boolean isMisc() {
        return SubType.current == SubType.MISC;
    }

    public static boolean isGame() {
        return !isMisc() && !isLobby() && !isHub();
    }

    public static boolean isHub() {
        return SubType.current == SubType.MISC
                && GameType.current == GameType.HUB;
    }

    public static CoreAPI getCoreAPI() {
        if (coreAPI == null)
            throw new ApiNotLoadedException();
        return coreAPI;
    }

    public static GamerManager getGamerManager() {
        if (gamerManagerAPI == null)
            throw new ApiNotLoadedException ();
        return gamerManagerAPI;
    }

    public static ParticleAPI getParticleAPI() {
        if (particleAPI == null)
            throw new ApiNotLoadedException ();
        return particleAPI;
    }

    public static UsableAPI getUsableAPI() {
        if (usableAPI == null)
            throw new ApiNotLoadedException ();
        return usableAPI;
    }

    public static JSONMessageAPI getJsonMessageAPI() {
        if (jsonMessageAPI == null)
            throw new ApiNotLoadedException ();
        return jsonMessageAPI;
    }

    public static SoundAPI getSoundAPI() {
        if (soundAPI == null)
            throw new ApiNotLoadedException ();
        return soundAPI;
    }

    public static TitleAPI getTitlesAPI() {
        if (titleAPI == null)
            throw new ApiNotLoadedException ();
        return titleAPI;
    }

    public static ActionBarAPI getActionBarAPI() {
        if (actionBarAPI == null)
            throw new ApiNotLoadedException ();
        return actionBarAPI;
    }

    public static BorderAPI getBorderAPI() {
        if (borderAPI == null)
            throw new ApiNotLoadedException ();
        return borderAPI;
    }

    public static CommandsAPI getCommandsAPI() {
        if (commandsAPI == null)
            throw new ApiNotLoadedException ();
        return commandsAPI;
    }

    public static HologramAPI getHologramAPI() {
        if (hologramAPI == null)
            throw new ApiNotLoadedException ();
        return hologramAPI;
    }

    public static EntityAPI getEntityAPI() {
        if (entityAPI == null)
            throw new ApiNotLoadedException ();
        return entityAPI;
    }

    public static InventoryAPI getInventoryAPI() {
        if (inventoryAPI == null)
            throw new ApiNotLoadedException ();
        return inventoryAPI;
    }

    public static ScoreBoardAPI getScoreBoardAPI() {
        if (scoreBoardAPI == null)
            throw new ApiNotLoadedException ();
        return scoreBoardAPI;
    }

    public static GuiManager<DefaultGui<?>> getGuiManager() {
        if (guiManager == null)
            throw new ApiNotLoadedException ();
        return guiManager;
    }
}
