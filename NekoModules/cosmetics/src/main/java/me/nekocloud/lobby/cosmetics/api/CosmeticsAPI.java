package me.nekocloud.lobby.cosmetics.api;

import lombok.experimental.UtilityClass;
import me.nekocloud.lobby.cosmetics.api.manager.CosmeticManager;
import me.nekocloud.lobby.cosmetics.manager.CraftCosmeticManager;

@UtilityClass
public final class CosmeticsAPI {

    private final CosmeticManager COSMETIC_MANAGER = new CraftCosmeticManager();

    public CosmeticManager getCosmeticManager() {
        return COSMETIC_MANAGER;
    }
}

