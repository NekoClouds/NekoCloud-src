package me.nekocloud.survival.commons.api;

import org.bukkit.Location;

public interface Home {

    /**
     * получить имя дома
     * @return - имя
     */
    String getName();

    /**
     * получить локацию дома
     * @return - локация
     */
    Location getLocation();
}
