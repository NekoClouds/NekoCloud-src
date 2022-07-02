package me.nekocloud.packetlib.libraries.effect.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
@Getter
public abstract class CraftParticleData {

    @Setter
    private Material material;
    private final int[] packetData;

    public CraftParticleData(Material material, byte data) {
        this.material = material;
        this.packetData = new int[] { material.getId(), data };
    }
}
