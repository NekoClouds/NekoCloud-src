package me.nekocloud.api.hologram;

import me.nekocloud.api.entity.stand.CustomStand;

public interface HoloLine {

    CustomStand getCustomStand();

    /**
     * удалить строчку
     */
    void delete();
}
