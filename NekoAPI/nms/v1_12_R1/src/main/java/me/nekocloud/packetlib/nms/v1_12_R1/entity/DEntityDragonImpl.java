package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.api.entity.npc.types.EnderDragonNPC;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityDragon;
import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.World;

public class DEntityDragonImpl extends DEntityLivingBase<EntityEnderDragon> implements DEntityDragon {

    public DEntityDragonImpl(World world) {
        super(new EntityEnderDragon(world));
    }

    @Override
    public EnderDragonNPC.Phase getPhase() {
        return EnderDragonNPC.Phase.values()[entity.getDataWatcher().get(EntityEnderDragon.PHASE)];
    }

    @Override
    public void setPhase(EnderDragonNPC.Phase phase) {
        entity.getDragonControllerManager().setControllerPhase(getMinecraftPhase(phase));
    }

    private static DragonControllerPhase<?> getMinecraftPhase(EnderDragonNPC.Phase phase) {
        return DragonControllerPhase.getById(phase.ordinal());
    }
}
