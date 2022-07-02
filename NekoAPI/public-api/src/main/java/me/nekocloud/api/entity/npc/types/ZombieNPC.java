package me.nekocloud.api.entity.npc.types;

import me.nekocloud.api.entity.npc.NPC;

public interface ZombieNPC extends NPC {
    boolean isBaby();

    void setBaby(boolean baby);
}
