package me.nekocloud.api.hologram;

import org.bukkit.Location;

import java.util.List;

public interface HologramAPI {

    /**
     * Создать голограмму
     * по умолчанию скрыта для всех
     * @param location - локация для создания голограммы
     * @return - вернет Hologram
     */
    Hologram createHologram(Location location);

    /**
     * получить все голограммы
     * @return - голограммы
     */
    List<Hologram> getHolograms();
}
