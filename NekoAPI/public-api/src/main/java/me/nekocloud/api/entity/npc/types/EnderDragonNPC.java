package me.nekocloud.api.entity.npc.types;

import me.nekocloud.api.entity.npc.NPC;

public interface EnderDragonNPC extends NPC {
    Phase getPhase();

    void setPhase(Phase phase);

    enum Phase {
        CIRCLING,
        STRAFING,
        FLY_TO_PORTAL,
        LAND_ON_PORTAL,
        LEAVE_PORTAL,
        BREATH_ATTACK,
        SEARCH_FOR_BREATH_ATTACK_TARGET,
        ROAR_BEFORE_ATTACK,
        CHARGE_PLAYER,
        DYING,
        HOVER;

    }
}
