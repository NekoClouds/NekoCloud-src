package me.nekocloud.survival.commons.api;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import me.nekocloud.survival.commons.api.manager.CommonsBoardManager;
import me.nekocloud.survival.commons.managers.*;
import me.nekocloud.survival.commons.api.manager.KitManager;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.api.manager.GuiManager;
import org.bukkit.Location;

@UtilityClass
public final class CommonsSurvivalAPI {
    private UserManager userManager;
    private WarpManager warpManager;
    private KitManager kitManager;
    private GuiManager guiManager;
    private CommonsBoardManager commonsBoardManager;

    @Setter
    private static Location spawn;

    /**
     * Интерфейс для работы с User'ами
     * @return - UserManager
     */
    public static UserManager getUserManager() {
        if (userManager == null)
            userManager = new CraftUserManager();

        return userManager;
    }

    public static Location getSpawn() {
        return spawn.clone();
    }

    /**
     * Интерфейс для работы с warp'ами
     * @return - WarpManager
     */
    public static WarpManager getWarpManager() {
        if (warpManager == null)
            warpManager = new CraftWarpManager();

        return warpManager;
    }

    /**
     * Интерфейс для работы с kit'ами
     * @return - KitManager
     */
    public static KitManager getKitManager() {
        if (kitManager == null)
            kitManager = new CraftKitManager();

        return kitManager;
    }

    /**
     * интерфейс для работы с menu
     * @return - guimanager
     */
    public static GuiManager<CommonsSurvivalGui> getGuiManager() {
        if (guiManager == null)
            guiManager = new CraftCommonsGuiManager();

        return guiManager;
    }

    /**
     * Интерфейс для работы с бордами сурвача
     * @return commonsBoardManager
     */
    public static CommonsBoardManager getBoardManager() {
        if (commonsBoardManager == null)
            commonsBoardManager = new CraftCommonsCommonsBoardManager();

        return commonsBoardManager;
    }


}
