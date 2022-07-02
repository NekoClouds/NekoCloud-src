package me.nekocloud.api.depend;

import org.bukkit.Location;

public interface PacketEntity extends PacketObject {
    /**
     * получить айди энтити
     * @return - энтити айди
     */
    int getEntityID();

    /**
     * Получить локацию энтити
     * @return - локация энтити
     */
    Location getLocation();

    /**
     * телепортировать энтити куда-то
     * @param location - куда телепортировать
     */
    void onTeleport(Location location);
}
