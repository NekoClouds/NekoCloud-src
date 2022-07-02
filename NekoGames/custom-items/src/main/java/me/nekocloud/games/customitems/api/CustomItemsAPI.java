package me.nekocloud.games.customitems.api;

import lombok.experimental.UtilityClass;
import me.nekocloud.games.customitems.manager.CustomItemsManager;

@UtilityClass
public class CustomItemsAPI {

    private final CustomItemsManager MORE_ITEMS_MANAGER = new CustomItemsManager();

    public CustomItemsManager getItemsManager() {
        return MORE_ITEMS_MANAGER;
    }
}
