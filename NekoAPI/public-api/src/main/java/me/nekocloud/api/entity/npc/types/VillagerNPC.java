package me.nekocloud.api.entity.npc.types;

import me.nekocloud.api.entity.npc.NPC;

public interface VillagerNPC extends NPC {

    /**
     * узнать професси
     * @return - профессия
     */
    Profession getProfession();

    /**
     * только для VILLAGER
     * задать тип жителя
     */
    void setVillagerProfession(Profession profession);

    enum Profession {
        NORMAL,
        FARMER,
        LIBRARIAN,
        PRIEST,
        BLACKSMITH,
        BUTCHER,
        NITWIT,
        HUSK
    }
}
