package me.nekocloud.packetlib.libraries.effect.data;

import org.bukkit.Material;

public class CraftParticleBlockData extends CraftParticleData {

    public CraftParticleBlockData(Material material, byte data) {
        super(material, data);


        if (material.isBlock()) {
            return;
        }

        setMaterial(Material.STONE); //если вдруг ты даун и укажешь не блок, то это фикс
    }
}
