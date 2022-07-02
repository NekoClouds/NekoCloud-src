package me.nekocloud.packetlib.libraries.hologram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HologramAPIImpl implements HologramAPI {

    NekoAPI nekoAPI;
    HologramManager hologramManager;

    public HologramAPIImpl(final NekoAPI nekoAPI) {
        this.nekoAPI = nekoAPI;

        this.hologramManager = new HologramManager();

        new HologramListener(this);

        Bukkit.getScheduler().runTaskTimer(nekoAPI, new HologramTask(hologramManager), 0, 1L);
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleAtFixedRate(new HologramTask(hologramManager), 0, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public Hologram createHologram(final Location location) {
        return new CraftHologram(hologramManager, location);
    }

    @Override
    public List<Hologram> getHolograms() {
        return new ArrayList<>(hologramManager.getHolograms());
    }
}
