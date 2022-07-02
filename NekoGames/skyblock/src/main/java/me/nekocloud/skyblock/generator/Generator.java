package me.nekocloud.skyblock.generator;

import org.bukkit.Material;

public interface Generator {

    /**
     * получить блок
     * @param chance - шанс который пришел
     * @return - блок
     */
    Material getBlock(double chance);
}
