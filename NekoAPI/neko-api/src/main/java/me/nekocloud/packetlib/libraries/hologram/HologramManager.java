package me.nekocloud.packetlib.libraries.hologram;

import io.netty.util.internal.ConcurrentSet;
import me.nekocloud.packetlib.libraries.hologram.lines.CraftHoloLine;
import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.api.hologram.Hologram;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager {

    private final Set<CraftHologram> holograms = new ConcurrentSet<>();
    private final Map<CustomStand, Hologram> holograms_by_stand = new ConcurrentHashMap<>();

    void addHologram(CraftHologram hologram) {
        holograms.add(hologram);
    }

    public void addCustomStand(Hologram hologram, CustomStand customStand) {
        holograms_by_stand.put(customStand, hologram);
    }

    public void removeCustomStand(CustomStand customStand) {
        holograms_by_stand.remove(customStand);
    }

    Set<CraftHologram> getHolograms() {
        return holograms;
    }

    Map<CustomStand, Hologram> getHologramByStand() {
        return holograms_by_stand;
    }

    void removeHologram(CraftHologram hologram) {
        holograms.remove(hologram);
        for (CraftHoloLine craftHoloLine : hologram.getLines()){
            Bukkit.getOnlinePlayers().forEach(craftHoloLine::hideTo);
            craftHoloLine.remove();
        }
    }
}
