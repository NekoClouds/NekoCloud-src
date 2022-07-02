package me.nekocloud.api.entity.npc.types;

import me.nekocloud.api.entity.npc.NPC;

public interface CreeperNPC extends NPC {

    /**
     * надутый или нет
     * @return - крипер
     */
    boolean isPowered();

    /**
     * сделать надутым
     * @param flag - какой крипер сейчас
     */
    void setPowered(boolean flag);
}
