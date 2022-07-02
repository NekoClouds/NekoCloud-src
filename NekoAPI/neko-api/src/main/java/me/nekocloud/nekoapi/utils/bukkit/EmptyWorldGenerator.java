package me.nekocloud.nekoapi.utils.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator {

    @SuppressWarnings("deprecation")
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ,
                                          ChunkGenerator.BiomeGrid biomeGrid) {
        return new byte[world.getMaxHeight() / 16][];
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.0, 0.0, 0.0);
    }
}
